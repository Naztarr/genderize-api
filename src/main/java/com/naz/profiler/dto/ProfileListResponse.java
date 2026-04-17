package com.naz.profiler.dto;

import java.util.List;

public record ProfileListResponse(
        String status,
        Integer count,
        List<ProfileList> profile
) implements ApiResponse {
    public ProfileListResponse(Integer count, List<ProfileList> profile){
        this("success",count, profile);
    }
}
