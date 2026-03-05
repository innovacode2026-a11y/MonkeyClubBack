package com.monkeyclub.gym.features.sales;

import com.monkeyclub.gym.application.port.in.sales.SaleUseCase;

import com.monkeyclub.gym.features.client.Client;
import com.monkeyclub.gym.features.client.ClientRepository;
import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.common.PaymentMethod;
import com.monkeyclub.gym.features.inventory.Product;
import com.monkeyclub.gym.features.inventory.ProductService;
import com.monkeyclub.gym.features.membership.Membership;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService implements SaleUseCase {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final ProductService productService;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public SaleService(SaleRepository saleRepository,
                       ClientRepository clientRepository,
                       ProductService productService,
                       CurrentUserService currentUserService,
                       RolePermissionService rolePermissionService) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.productService = productService;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional
    public SaleResponse createProductSale(CreateProductSaleRequest request) {
        User actor = require(SystemModule.VENTAS, PermissionAction.CREAR);

        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber("POS"));
        sale.setType(SaleType.PRODUCT);
        sale.setStatus(SaleStatus.COMPLETADA);
        sale.setPaymentMethod(request.paymentMethod());
        sale.setNotes(normalizeNullable(request.notes()));
        sale.setCreatedBy(actor);

        if (request.clientId() != null) {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
            sale.setClient(client);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ProductSaleItemRequest itemRequest : request.items()) {
            Product product = productService.findProduct(itemRequest.productId());
            productService.consumeStockForSale(product.getId(), itemRequest.quantity(), actor);

            SaleItem item = new SaleItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));

            total = total.add(item.getSubtotal());
            sale.addItem(item);
        }

        sale.setTotalAmount(total);
        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public Sale recordMembershipSale(Membership membership, PaymentMethod paymentMethod, String notes, User actor) {
        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber("MEM"));
        sale.setType(SaleType.MEMBERSHIP);
        sale.setStatus(SaleStatus.COMPLETADA);
        sale.setClient(membership.getClient());
        sale.setMembership(membership);
        sale.setPaymentMethod(paymentMethod);
        sale.setNotes(normalizeNullable(notes));
        sale.setCreatedBy(actor);
        sale.setTotalAmount(membership.getPlan().getPrice());

        return saleRepository.save(sale);
    }

    @Transactional
    public SaleResponse annulSale(UUID saleId, AnnulSaleRequest request) {
        User actor = require(SystemModule.VENTAS, PermissionAction.ELIMINAR);

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        if (sale.getStatus() == SaleStatus.ANULADA) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La venta ya esta anulada");
        }

        if (sale.getType() == SaleType.PRODUCT) {
            for (SaleItem item : sale.getItems()) {
                productService.restoreStockFromCancelledSale(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        actor,
                        "Anulacion de venta " + sale.getSaleNumber());
            }
        }

        sale.setStatus(SaleStatus.ANULADA);
        sale.setAnnulmentReason(request.reason().trim());
        sale.setAnnulledBy(actor);

        return toResponse(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> listSales(LocalDate from, LocalDate to) {
        require(SystemModule.VENTAS, PermissionAction.VER);

        LocalDate effectiveFrom = from == null ? LocalDate.now(ZoneOffset.UTC).minusDays(30) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now(ZoneOffset.UTC) : to;

        OffsetDateTime fromDateTime = effectiveFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDateTime = effectiveTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        return saleRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(fromDateTime, toDateTime)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = new ArrayList<>();
        for (SaleItem item : sale.getItems()) {
            if (item.getProduct() != null) {
                itemResponses.add(new SaleItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()));
            }
        }

        String clientName = sale.getClient() == null
                ? null
                : sale.getClient().getFirstName() + " " + sale.getClient().getLastName();

        return new SaleResponse(
                sale.getId(),
                sale.getSaleNumber(),
                sale.getType(),
                sale.getStatus(),
                clientName,
                sale.getPaymentMethod(),
                sale.getTotalAmount(),
                sale.getNotes(),
                sale.getAnnulmentReason(),
                sale.getCreatedAt(),
                itemResponses);
    }

    private String generateSaleNumber(String prefix) {
        long timestamp = System.currentTimeMillis();
        int suffix = (int) (Math.random() * 9000) + 1000;
        return prefix + "-" + timestamp + "-" + suffix;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
