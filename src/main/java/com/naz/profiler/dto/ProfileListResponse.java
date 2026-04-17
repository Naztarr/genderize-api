package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProfileListResponse(
        String status,
        Integer count,
        @JsonProperty("data")
        List<ProfileList> profile
) implements ApiResponse {
    public ProfileListResponse(Integer count, List<ProfileList> profile){
        this("success",count, profil);
    }
}
