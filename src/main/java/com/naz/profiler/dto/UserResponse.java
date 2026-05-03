package com.naz.profiler.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String avatarUrl,
        String role,
        Instant lastLoginAt
) {
}
