package com.monkeyclub.gym.application.port.in.inventory;

import com.monkeyclub.gym.features.inventory.InventoryAdjustmentRequest;
import com.monkeyclub.gym.features.inventory.InventoryEntryRequest;
import com.monkeyclub.gym.features.inventory.InventoryMovementResponse;
import com.monkeyclub.gym.features.inventory.ProductRequest;
import com.monkeyclub.gym.features.inventory.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductUseCase {

    ProductResponse create(ProductRequest request);

    ProductResponse update(UUID productId, ProductRequest request);

    List<ProductResponse> list();

    List<ProductResponse> lowStock();

    InventoryMovementResponse registerEntry(InventoryEntryRequest request);

    InventoryMovementResponse registerAdjustment(InventoryAdjustmentRequest request);

    List<InventoryMovementResponse> movementHistory(UUID productId);
}
