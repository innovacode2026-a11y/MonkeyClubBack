package com.monkeyclub.gym.sales;

import com.monkeyclub.gym.client.Client;
import com.monkeyclub.gym.common.BaseEntity;
import com.monkeyclub.gym.common.PaymentMethod;
import com.monkeyclub.gym.membership.Membership;
import com.monkeyclub.gym.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String saleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status = SaleStatus.COMPLETADA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 2000)
    private String notes;

    @Column(length = 1000)
    private String annulmentReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annulled_by")
    private User annulledBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    public void addItem(SaleItem item) {
        item.setSale(this);
        this.items.add(item);
    }
}
