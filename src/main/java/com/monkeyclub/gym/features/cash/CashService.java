package com.monkeyclub.gym.features.cash;

import com.monkeyclub.gym.application.port.in.cash.CashUseCase;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.features.sales.Sale;
import com.monkeyclub.gym.features.sales.SaleRepository;
import com.monkeyclub.gym.features.sales.SaleStatus;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import com.monkeyclub.gym.features.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class CashService implements CashUseCase {

    private final CashSessionRepository cashSessionRepository;
    private final SaleRepository saleRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public CashService(CashSessionRepository cashSessionRepository,
                       SaleRepository saleRepository,
                       CurrentUserService currentUserService,
                       RolePermissionService rolePermissionService) {
        this.cashSessionRepository = cashSessionRepository;
        this.saleRepository = saleRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional
    public CashSessionResponse openCash(OpenCashRequest request) {
        User actor = require(SystemModule.PAGOS_CAJA, PermissionAction.CREAR);

        cashSessionRepository.findOpenByCashierId(actor.getId())
                .ifPresent(s -> {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Ya tienes una caja abierta");
                });

        CashSession session = new CashSession();
        session.setCashier(actor);
        session.setOpeningAmount(request.openingAmount());
        session.setOpenedAt(OffsetDateTime.now(ZoneOffset.UTC));
        session.setStatus(CashSessionStatus.ABIERTA);

        return toResponse(cashSessionRepository.save(session));
    }

    @Transactional
    public CashSessionResponse closeCash(UUID sessionId, CloseCashRequest request) {
        User actor = require(SystemModule.PAGOS_CAJA, PermissionAction.EDITAR);

        CashSession session = cashSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Caja no encontrada"));

        if (session.getStatus() != CashSessionStatus.ABIERTA) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La caja ya esta cerrada");
        }

        boolean isOwner = session.getCashier().getId().equals(actor.getId());
        if (!isOwner && actor.getRole() != UserRole.ADMIN) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "No puedes cerrar la caja de otro usuario");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Sale> sales = saleRepository.findByCreatedByIdAndCreatedAtBetweenAndStatus(
                session.getCashier().getId(),
                session.getOpenedAt(),
                now,
                SaleStatus.COMPLETADA.name());

        BigDecimal salesTotal = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expected = session.getOpeningAmount().add(salesTotal);
        BigDecimal difference = request.closingAmount().subtract(expected);

        session.setClosingAmount(request.closingAmount());
        session.setExpectedAmount(expected);
        session.setDifference(difference);
        session.setClosedAt(now);
        session.setStatus(CashSessionStatus.CERRADA);

        return toResponse(cashSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public CashSessionResponse currentCash() {
        User actor = require(SystemModule.PAGOS_CAJA, PermissionAction.VER);

        CashSession session = cashSessionRepository.findOpenByCashierId(actor.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "No hay caja abierta"));
        return toResponse(session);
    }

    @Transactional(readOnly = true)
    public List<CashSessionResponse> history() {
        User actor = require(SystemModule.PAGOS_CAJA, PermissionAction.VER);
        return cashSessionRepository.findByCashierIdOrderByOpenedAtDesc(actor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private CashSessionResponse toResponse(CashSession session) {
        return new CashSessionResponse(
                session.getId(),
                session.getCashier().getUsername(),
                session.getOpeningAmount(),
                session.getClosingAmount(),
                session.getExpectedAmount(),
                session.getDifference(),
                session.getOpenedAt(),
                session.getClosedAt(),
                session.getStatus());
    }
}
