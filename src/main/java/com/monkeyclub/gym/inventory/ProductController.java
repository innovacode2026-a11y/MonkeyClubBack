package com.monkeyclub.gym.inventory;

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

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable UUID productId,
                                  @Valid @RequestBody ProductRequest request) {
        return productService.update(productId, request);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productService.list();
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock() {
        return productService.lowStock();
    }

    @PostMapping("/inventory/entries")
    public InventoryMovementResponse registerEntry(@Valid @RequestBody InventoryEntryRequest request) {
        return productService.registerEntry(request);
    }

    @PostMapping("/inventory/adjustments")
    public InventoryMovementResponse registerAdjustment(@Valid @RequestBody InventoryAdjustmentRequest request) {
        return productService.registerAdjustment(request);
    }

    @GetMapping("/{productId}/movements")
    public List<InventoryMovementResponse> movements(@PathVariable UUID productId) {
        return productService.movementHistory(productId);
    }
}
