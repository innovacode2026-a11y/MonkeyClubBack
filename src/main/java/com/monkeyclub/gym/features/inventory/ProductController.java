package com.monkeyclub.gym.features.inventory;

import com.monkeyclub.gym.application.port.in.inventory.ProductUseCase;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productUseCase.create(request);
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable UUID productId,
                                  @Valid @RequestBody ProductRequest request) {
        return productUseCase.update(productId, request);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productUseCase.list();
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock() {
        return productUseCase.lowStock();
    }

    @PostMapping("/inventory/entries")
    public InventoryMovementResponse registerEntry(@Valid @RequestBody InventoryEntryRequest request) {
        return productUseCase.registerEntry(request);
    }

    @PostMapping("/inventory/adjustments")
    public InventoryMovementResponse registerAdjustment(@Valid @RequestBody InventoryAdjustmentRequest request) {
        return productUseCase.registerAdjustment(request);
    }

    @GetMapping("/{productId}/movements")
    public List<InventoryMovementResponse> movements(@PathVariable UUID productId) {
        return productUseCase.movementHistory(productId);
    }
}
