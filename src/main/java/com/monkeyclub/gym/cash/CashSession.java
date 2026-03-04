package com.monkeyclub.gym.cash;

import com.monkeyclub.gym.common.BaseEntity;
import com.monkeyclub.gym.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "cash_sessions")
public class CashSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cashier_id")
    private User cashier;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal openingAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal closingAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal expectedAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal difference;

    @Column(nullable = false)
    private OffsetDateTime openedAt;

    private OffsetDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashSessionStatus status;
}
