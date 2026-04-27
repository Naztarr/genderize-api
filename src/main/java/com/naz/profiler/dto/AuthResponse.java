package com.naz.profiler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String status;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
}
