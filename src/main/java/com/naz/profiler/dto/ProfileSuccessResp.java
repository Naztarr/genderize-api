package com.naz.profiler.dto;

public record ProfileSuccessResp(String status, ProfileResponseData data) implements ApiResponse {
    public ProfileSuccessResp(ProfileResponseData data){
        this("success", data);
    }
}
