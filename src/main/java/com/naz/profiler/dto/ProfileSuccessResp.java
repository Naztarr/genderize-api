package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileSuccessResp(String status,
                                 ProfileResponseData data) implements ApiResponse     {
    public ProfileSuccessResp(ProfileResponseData data){
        this("success", data);
    }
}
