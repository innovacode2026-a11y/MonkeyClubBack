package com.monkeyclub.gym.features.membership;

import com.monkeyclub.gym.application.port.in.membership.MembershipUseCase;

import com.monkeyclub.gym.features.client.Client;
import com.monkeyclub.gym.features.client.ClientRepository;
import com.monkeyclub.gym.features.client.ClientStatus;
import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.features.plan.MembershipPlan;
import com.monkeyclub.gym.features.plan.MembershipPlanRepository;
import com.monkeyclub.gym.features.sales.Sale;
import com.monkeyclub.gym.features.sales.SaleService;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MembershipService implements MembershipUseCase {

    private final MembershipRepository membershipRepository;
    private final MembershipHistoryRepository historyRepository;
    private final MembershipPlanRepository planRepository;
    private final ClientRepository clientRepository;
    private final SaleService saleService;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public MembershipService(MembershipRepository membershipRepository,
                             MembershipHistoryRepository historyRepository,
                             MembershipPlanRepository planRepository,
                             ClientRepository clientRepository,
                             SaleService saleService,
                             CurrentUserService currentUserService,
                             RolePermissionService rolePermissionService) {
        this.membershipRepository = membershipRepository;
        this.historyRepository = historyRepository;
        this.planRepository = planRepository;
        this.clientRepository = clientRepository;
        this.saleService = saleService;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional
    public MembershipResponse sell(SellMembershipRequest request) {
        return createOrRenew(request, MembershipHistoryAction.VENTA);
    }

    @Transactional
    public MembershipResponse renew(SellMembershipRequest request) {
        return createOrRenew(request, MembershipHistoryAction.RENOVACION);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getByClient(UUID clientId) {
        require(SystemModule.MEMBRESIAS, PermissionAction.VER);
        return membershipRepository.findByClientIdOrderByEndDateDesc(clientId)
                .stream()
                .map(m -> toResponse(m, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembershipHistoryResponse> getHistory(UUID clientId) {
        require(SystemModule.MEMBRESIAS, PermissionAction.VER);
        return historyRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(h -> new MembershipHistoryResponse(
                        h.getId(),
                        h.getAction(),
                        h.getPlan().getName(),
                        h.getStartDate(),
                        h.getPreviousEndDate(),
                        h.getNewEndDate(),
                        h.getReceiptNumber(),
                        h.getPerformedBy() == null ? "sistema" : h.getPerformedBy().getUsername(),
                        h.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void refreshExpiredStatusesManual() {
        require(SystemModule.MEMBRESIAS, PermissionAction.EDITAR);
        refreshExpiredStatusesInternal();
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void refreshExpiredStatuses() {
        refreshExpiredStatusesInternal();
    }

    private void refreshExpiredStatusesInternal() {
        LocalDate today = LocalDate.now();

        List<Membership> expired = membershipRepository.findByEndDateBeforeAndStatus(today, MembershipStatus.ACTIVA.name());
        for (Membership membership : expired) {
            membership.setStatus(MembershipStatus.VENCIDA);

            Client client = membership.getClient();
            Membership current = membershipRepository.findTopByClientIdOrderByEndDateDesc(client.getId()).orElse(null);
            if (current == null || current.getEndDate() == null || current.getEndDate().isBefore(today)) {
                client.setStatus(ClientStatus.VENCIDO);
                clientRepository.save(client);
            }
        }

        membershipRepository.saveAll(expired);
    }

    private MembershipResponse createOrRenew(SellMembershipRequest request, MembershipHistoryAction action) {
        User actor = require(SystemModule.MEMBRESIAS, PermissionAction.CREAR);

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        MembershipPlan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Plan no encontrado"));

        if (!plan.isActive()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El plan esta inactivo");
        }

        LocalDate today = LocalDate.now();
        Membership existing = membershipRepository.findTopByClientIdOrderByEndDateDesc(client.getId()).orElse(null);

        Membership membership = existing == null ? new Membership() : existing;

        LocalDate previousEndDate = membership.getEndDate();
        LocalDate startDate = today;
        if (previousEndDate != null && !previousEndDate.isBefore(today)) {
            startDate = previousEndDate.plusDays(1);
        }

        LocalDate endDate = startDate.plusDays(plan.getDurationDays() - 1L);

        membership.setClient(client);
        membership.setPlan(plan);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setStatus(MembershipStatus.ACTIVA);
        membership.setCreatedBy(actor);

        Membership savedMembership = membershipRepository.save(membership);

        Sale sale = saleService.recordMembershipSale(savedMembership, request.paymentMethod(), request.notes(), actor);

        MembershipHistory history = new MembershipHistory();
        history.setMembership(savedMembership);
        history.setClient(client);
        history.setPlan(plan);
        history.setStartDate(startDate);
        history.setPreviousEndDate(previousEndDate);
        history.setNewEndDate(endDate);
        history.setAction(action);
        history.setPerformedBy(actor);
        history.setReceiptNumber(sale.getSaleNumber());
        historyRepository.save(history);

        client.setStatus(ClientStatus.ACTIVO);
        clientRepository.save(client);

        return toResponse(savedMembership, sale.getSaleNumber());
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private MembershipResponse toResponse(Membership membership, String receiptNumber) {
        Client client = membership.getClient();
        MembershipPlan plan = membership.getPlan();
        return new MembershipResponse(
                membership.getId(),
                client.getId(),
                client.getFirstName() + " " + client.getLastName(),
                client.getStatus(),
                plan.getId(),
                plan.getName(),
                plan.getPrice(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getStatus(),
                receiptNumber);
    }
}
