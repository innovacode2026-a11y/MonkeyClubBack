package com.monkeyclub.gym.features.sales;

import com.monkeyclub.gym.application.port.in.sales.SaleUseCase;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleUseCase saleUseCase;

    public SaleController(SaleUseCase saleUseCase) {
        this.saleUseCase = saleUseCase;
    }

    @PostMapping("/products")
    public SaleResponse createProductSale(@Valid @RequestBody CreateProductSaleRequest request) {
        return saleUseCase.createProductSale(request);
    }

    @PatchMapping("/{saleId}/annul")
    public SaleResponse annulSale(@PathVariable UUID saleId,
                                  @Valid @RequestBody AnnulSaleRequest request) {
        return saleUseCase.annulSale(saleId, request);
    }

    @GetMapping
    public List<SaleResponse> listSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return saleUseCase.listSales(from, to);
    }
}
