package com.naz.profiler.dto;

import java.util.List;

public record PageProfileResponse(String status,
                                  Integer page,
                                  Integer limit,
                                  long total,
                                  List<ProfileResponseData> data) implements ApiResponse{
    public PageProfileResponse(Integer page, Integer limit, long total, List<ProfileResponseData> data)
    {this("success",page,limit,total,data);}
}
