package com.monkeyclub.gym.inventory;

import com.monkeyclub.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(unique = true)
    private String barcode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Integer minStock = 0;

    @Column(nullable = false)
    private boolean active = true;
}
