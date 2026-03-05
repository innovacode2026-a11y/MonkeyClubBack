package com.monkeyclub.gym.features.permission;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class RolePermissionService {

    private final Map<UserRole, Map<SystemModule, Set<PermissionAction>>> rolePermissions =
            new EnumMap<>(UserRole.class);

    public RolePermissionService() {
        configureAdmin();
        configureRecepcion();
        configureCajero();
        configureEntrenador();
    }

    public boolean hasPermission(UserRole role, SystemModule module, PermissionAction action) {
        return rolePermissions.getOrDefault(role, Map.of())
                .getOrDefault(module, Set.of())
                .contains(action);
    }

    public void requirePermission(UserRole role, SystemModule module, PermissionAction action) {
        if (!hasPermission(role, module, action)) {
            throw new BusinessException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para " + action + " en el modulo " + module);
        }
    }

    public Map<SystemModule, Set<PermissionAction>> permissionsFor(UserRole role) {
        return rolePermissions.getOrDefault(role, Map.of());
    }

    private void configureAdmin() {
        Map<SystemModule, Set<PermissionAction>> map = new EnumMap<>(SystemModule.class);
        for (SystemModule module : SystemModule.values()) {
            map.put(module, EnumSet.allOf(PermissionAction.class));
        }
        rolePermissions.put(UserRole.ADMIN, map);
    }

    private void configureRecepcion() {
        Map<SystemModule, Set<PermissionAction>> map = new EnumMap<>(SystemModule.class);
        map.put(SystemModule.CLIENTES, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR, PermissionAction.EDITAR));
        map.put(SystemModule.MEMBRESIAS, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR, PermissionAction.EDITAR));
        map.put(SystemModule.CONTROL_INGRESO, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR));
        map.put(SystemModule.REPORTES, EnumSet.of(PermissionAction.VER));
        rolePermissions.put(UserRole.RECEPCION, map);
    }

    private void configureCajero() {
        Map<SystemModule, Set<PermissionAction>> map = new EnumMap<>(SystemModule.class);
        map.put(SystemModule.PAGOS_CAJA, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR, PermissionAction.EDITAR));
        map.put(SystemModule.VENTAS, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR, PermissionAction.EDITAR));
        map.put(SystemModule.CLIENTES, EnumSet.of(PermissionAction.VER));
        map.put(SystemModule.MEMBRESIAS, EnumSet.of(PermissionAction.VER, PermissionAction.CREAR));
        rolePermissions.put(UserRole.CAJERO, map);
    }

    private void configureEntrenador() {
        Map<SystemModule, Set<PermissionAction>> map = new EnumMap<>(SystemModule.class);
        map.put(SystemModule.CLIENTES, EnumSet.of(PermissionAction.VER));
        map.put(SystemModule.CONTROL_INGRESO, EnumSet.of(PermissionAction.VER));
        rolePermissions.put(UserRole.ENTRENADOR, map);
    }
}
