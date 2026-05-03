package com.naz.profiler.dto;

import lombok.Data;

@Data
public class CliExchangeRequest {
    private String code;
    private String verifier;
}
