package com.naz.profiler.dto;

import java.util.List;

public record ProfileListResponse(
        String status,
        Integer count,
        List<ProfileList> profiles
) implements ApiResponse {
    public ProfileListResponse(Integer count, List<ProfileList> profiles){
        this("success",count, profiles);
    }
}
