package com.naz.profiler.dto;



public record SuccessResponse(String status, ResponseData data) implements ApiResponse {
    public SuccessResponse(ResponseData data){
        this("success", data);
    }
}
