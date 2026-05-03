package com.naz.profiler.dto;

import java.util.List;
import java.util.Map;

public record PageProfileResponse(String status,
                                  Integer page,
                                  Integer limit,
                                  long total,
                                  Integer totalPages,
                                  Map<String,String> links,
                                  List<ProfileResponseData> data) implements ApiResponse{
    public PageProfileResponse(Integer page, Integer limit, long total, Integer totalPages,
                               Map<String, String> links, List<ProfileResponseData> data)
    {this("success",page,limit,total,totalPages, links, data);}
}
