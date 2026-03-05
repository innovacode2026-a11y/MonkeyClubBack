package com.monkeyclub.gym.features.report;

import com.monkeyclub.gym.application.port.in.report.ReportUseCase;

import com.monkeyclub.gym.common.PaymentMethod;
import com.monkeyclub.gym.features.membership.Membership;
import com.monkeyclub.gym.features.membership.MembershipRepository;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.features.sales.Sale;
import com.monkeyclub.gym.features.sales.SaleRepository;
import com.monkeyclub.gym.features.sales.SaleStatus;
import com.monkeyclub.gym.features.sales.SaleType;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService implements ReportUseCase {

    private final SaleRepository saleRepository;
    private final MembershipRepository membershipRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public ReportService(SaleRepository saleRepository,
                         MembershipRepository membershipRepository,
                         CurrentUserService currentUserService,
                         RolePermissionService rolePermissionService) {
        this.saleRepository = saleRepository;
        this.membershipRepository = membershipRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional(readOnly = true)
    public IncomeReportResponse incomeReport(LocalDate from, LocalDate to) {
        require(SystemModule.REPORTES, PermissionAction.VER);

        LocalDate effectiveFrom = from == null ? LocalDate.now(ZoneOffset.UTC).minusDays(30) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now(ZoneOffset.UTC) : to;

        OffsetDateTime fromDateTime = effectiveFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDateTime = effectiveTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<Sale> sales = saleRepository.findByCreatedAtBetweenAndStatusOrderByCreatedAtDesc(
                fromDateTime,
                toDateTime,
                SaleStatus.COMPLETADA.name());

        BigDecimal membershipTotal = sales.stream()
                .filter(s -> s.getType() == SaleType.MEMBERSHIP)
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal productTotal = sales.stream()
                .filter(s -> s.getType() == SaleType.PRODUCT)
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentMethodTotal> byPaymentMethod = Arrays.stream(PaymentMethod.values())
                .map(method -> new PaymentMethodTotal(
                        method,
                        sales.stream()
                                .filter(s -> s.getPaymentMethod() == method)
                                .map(Sale::getTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .toList();

        return new IncomeReportResponse(
                effectiveFrom,
                effectiveTo,
                membershipTotal,
                productTotal,
                membershipTotal.add(productTotal),
                byPaymentMethod);
    }

    @Transactional(readOnly = true)
    public List<ExpirationReportItem> expirations(LocalDate from, LocalDate to) {
        require(SystemModule.REPORTES, PermissionAction.VER);

        LocalDate effectiveFrom = from == null ? LocalDate.now(ZoneOffset.UTC) : from;
        LocalDate effectiveTo = to == null ? effectiveFrom.plusDays(15) : to;

        return membershipRepository.findByEndDateBetweenOrderByEndDateAsc(effectiveFrom, effectiveTo)
                .stream()
                .map(this::toExpiration)
                .toList();
    }

    private ExpirationReportItem toExpiration(Membership membership) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        long days = ChronoUnit.DAYS.between(today, membership.getEndDate());
        return new ExpirationReportItem(
                membership.getClient().getId(),
                membership.getClient().getFirstName() + " " + membership.getClient().getLastName(),
                membership.getClient().getDocument(),
                membership.getClient().getPhone(),
                membership.getEndDate(),
                days,
                membership.getClient().getStatus());
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }
}
