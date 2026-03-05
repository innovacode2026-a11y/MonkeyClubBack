package com.monkeyclub.gym.features.permission;

import java.util.Set;

public record ModulePermissionResponse(
        SystemModule module,
        Set<PermissionAction> actions
) {
}
