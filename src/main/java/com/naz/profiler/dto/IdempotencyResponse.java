package com.naz.profiler.dto;

public record IdempotencyResponse(String status,
                                  String message,
                                  ProfileResponseData data) implements ApiResponse{
    public IdempotencyResponse(ProfileResponseData data){
        this("success", "Profile already exists", data);}
}
