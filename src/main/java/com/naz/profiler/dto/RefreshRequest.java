package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshRequest {
    @JsonProperty("refresh_token")
    private String refreshToken;
}
