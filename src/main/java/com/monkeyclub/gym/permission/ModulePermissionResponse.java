package com.monkeyclub.gym.permission;

import java.util.Set;

public record ModulePermissionResponse(
        SystemModule module,
        Set<PermissionAction> actions
) {
}
