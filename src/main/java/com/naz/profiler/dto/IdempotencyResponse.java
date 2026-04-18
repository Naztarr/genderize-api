package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IdempotencyResponse(String status,
                                  String message,
                                  @JsonProperty("data")
                                  ProfileResponseData data) implements ApiResponse{
    public IdempotencyResponse(ProfileResponseData data){
        this("success", "Profile already exists", data);}
}
