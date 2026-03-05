package com.monkeyclub.gym.application.port.in.sales;

import com.monkeyclub.gym.features.sales.AnnulSaleRequest;
import com.monkeyclub.gym.features.sales.CreateProductSaleRequest;
import com.monkeyclub.gym.features.sales.SaleResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SaleUseCase {

    SaleResponse createProductSale(CreateProductSaleRequest request);

    SaleResponse annulSale(UUID saleId, AnnulSaleRequest request);

    List<SaleResponse> listSales(LocalDate from, LocalDate to);
}
