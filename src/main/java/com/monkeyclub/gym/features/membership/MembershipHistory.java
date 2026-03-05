package com.monkeyclub.gym.features.membership;

import com.monkeyclub.gym.features.client.Client;
import com.monkeyclub.gym.common.BaseEntity;
import com.monkeyclub.gym.features.plan.MembershipPlan;
import com.monkeyclub.gym.features.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "membership_history")
public class MembershipHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id")
    private MembershipPlan plan;

    private LocalDate startDate;

    private LocalDate previousEndDate;

    private LocalDate newEndDate;

    @Enumerated(EnumType.STRING)
    private MembershipHistoryAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    private String receiptNumber;
}
