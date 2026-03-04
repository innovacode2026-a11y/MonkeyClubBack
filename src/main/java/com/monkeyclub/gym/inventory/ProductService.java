package com.monkeyclub.gym.inventory;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.permission.PermissionAction;
import com.monkeyclub.gym.permission.RolePermissionService;
import com.monkeyclub.gym.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public ProductService(ProductRepository productRepository,
                          InventoryMovementRepository movementRepository,
                          CurrentUserService currentUserService,
                          RolePermissionService rolePermissionService) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    public ProductResponse create(ProductRequest request) {
        require(SystemModule.INVENTARIO, PermissionAction.CREAR);
        validateBarcodeForCreate(request.barcode());

        Product product = new Product();
        product.setName(request.name().trim());
        product.setCategory(request.category().trim());
        product.setBarcode(normalizeNullable(request.barcode()));
        product.setPrice(request.price());
        product.setMinStock(request.minStock());
        product.setActive(request.active() == null || request.active());
        product.setStock(0);

        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(UUID productId, ProductRequest request) {
        require(SystemModule.INVENTARIO, PermissionAction.EDITAR);

        Product product = findProduct(productId);
        validateBarcodeForUpdate(request.barcode(), productId);

        product.setName(request.name().trim());
        product.setCategory(request.category().trim());
        product.setBarcode(normalizeNullable(request.barcode()));
        product.setPrice(request.price());
        product.setMinStock(request.minStock());
        if (request.active() != null) {
            product.setActive(request.active());
        }

        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> list() {
        require(SystemModule.INVENTARIO, PermissionAction.VER);
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> lowStock() {
        require(SystemModule.INVENTARIO, PermissionAction.VER);
        return productRepository.findAll().stream()
                .filter(p -> p.isActive() && p.getStock() <= p.getMinStock())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public InventoryMovementResponse registerEntry(InventoryEntryRequest request) {
        User actor = require(SystemModule.INVENTARIO, PermissionAction.CREAR);

        Product product = findProduct(request.productId());
        product.setStock(product.getStock() + request.quantity());

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(InventoryMovementType.ENTRADA);
        movement.setQuantity(request.quantity());
        movement.setProvider(normalizeNullable(request.provider()));
        movement.setReason(normalizeNullable(request.reason()));
        movement.setPerformedBy(actor);

        productRepository.save(product);
        InventoryMovement savedMovement = movementRepository.save(movement);
        return toMovementResponse(savedMovement);
    }

    @Transactional
    public InventoryMovementResponse registerAdjustment(InventoryAdjustmentRequest request) {
        User actor = require(SystemModule.INVENTARIO, PermissionAction.EDITAR);

        Product product = findProduct(request.productId());
        int signedQuantity = request.increase() ? request.quantity() : -request.quantity();
        int newStock = product.getStock() + signedQuantity;

        if (newStock < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El ajuste deja stock negativo");
        }

        product.setStock(newStock);

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(request.increase() ? InventoryMovementType.AJUSTE_POSITIVO : InventoryMovementType.AJUSTE_NEGATIVO);
        movement.setQuantity(request.quantity());
        movement.setReason(request.reason().trim());
        movement.setPerformedBy(actor);

        productRepository.save(product);
        InventoryMovement savedMovement = movementRepository.save(movement);
        return toMovementResponse(savedMovement);
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> movementHistory(UUID productId) {
        require(SystemModule.INVENTARIO, PermissionAction.VER);
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toMovementResponse)
                .toList();
    }

    @Transactional
    public void consumeStockForSale(UUID productId, Integer quantity, User actor) {
        Product product = findProduct(productId);
        if (product.getStock() < quantity) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para " + product.getName());
        }

        product.setStock(product.getStock() - quantity);

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(InventoryMovementType.VENTA);
        movement.setQuantity(quantity);
        movement.setReason("Salida por venta POS");
        movement.setPerformedBy(actor);

        productRepository.save(product);
        movementRepository.save(movement);
    }

    @Transactional
    public void restoreStockFromCancelledSale(UUID productId, Integer quantity, User actor, String reason) {
        Product product = findProduct(productId);
        product.setStock(product.getStock() + quantity);

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setType(InventoryMovementType.AJUSTE_POSITIVO);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        movement.setPerformedBy(actor);

        productRepository.save(product);
        movementRepository.save(movement);
    }

    public Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private void validateBarcodeForCreate(String barcode) {
        String normalized = normalizeNullable(barcode);
        if (normalized != null && productRepository.existsByBarcode(normalized)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Codigo de barras duplicado");
        }
    }

    private void validateBarcodeForUpdate(String barcode, UUID productId) {
        String normalized = normalizeNullable(barcode);
        if (normalized != null && productRepository.existsByBarcodeAndIdNot(normalized, productId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Codigo de barras duplicado");
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBarcode(),
                product.getPrice(),
                product.getStock(),
                product.getMinStock(),
                product.isActive());
    }

    private InventoryMovementResponse toMovementResponse(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getReason(),
                movement.getProvider(),
                movement.getPerformedBy() == null ? "sistema" : movement.getPerformedBy().getUsername(),
                movement.getCreatedAt());
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
