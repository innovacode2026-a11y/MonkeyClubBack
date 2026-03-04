package com.monkeyclub.gym.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        OffsetDateTime timestamp,
        List<String> details
) {
}
