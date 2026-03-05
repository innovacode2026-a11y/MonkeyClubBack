package com.monkeyclub.gym.features.plan;

import com.monkeyclub.gym.application.port.in.plan.PlanUseCase;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlanService implements PlanUseCase {

    private final MembershipPlanRepository planRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public PlanService(MembershipPlanRepository planRepository,
                       CurrentUserService currentUserService,
                       RolePermissionService rolePermissionService) {
        this.planRepository = planRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    public PlanResponse create(PlanRequest request) {
        require(SystemModule.PLANES, PermissionAction.CREAR);
        if (planRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un plan con ese nombre");
        }

        MembershipPlan plan = new MembershipPlan();
        plan.setName(request.name().trim());
        plan.setDescription(normalizeNullable(request.description()));
        plan.setDurationDays(request.durationDays());
        plan.setPrice(request.price());
        plan.setActive(request.active() == null || request.active());

        return toResponse(planRepository.save(plan));
    }

    public PlanResponse update(UUID planId, PlanRequest request) {
        require(SystemModule.PLANES, PermissionAction.EDITAR);

        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Plan no encontrado"));

        if (planRepository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), planId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un plan con ese nombre");
        }

        plan.setName(request.name().trim());
        plan.setDescription(normalizeNullable(request.description()));
        plan.setDurationDays(request.durationDays());
        plan.setPrice(request.price());
        if (request.active() != null) {
            plan.setActive(request.active());
        }

        return toResponse(planRepository.save(plan));
    }

    public List<PlanResponse> list() {
        require(SystemModule.PLANES, PermissionAction.VER);
        return planRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void delete(UUID planId) {
        require(SystemModule.PLANES, PermissionAction.ELIMINAR);
        planRepository.deleteById(planId);
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private PlanResponse toResponse(MembershipPlan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getDurationDays(),
                plan.getPrice(),
                plan.isActive());
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
