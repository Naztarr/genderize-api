package com.naz.profiler.dto;

import java.util.List;

public record ProfileListResponse(
        String status,
        Integer count,
        List<ProfileList> list
) implements ApiResponse {
    public ProfileListResponse(Integer count, List<ProfileList> list){
        this("success",count, list);
    }
}
