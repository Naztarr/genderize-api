package com.naz.genderize.dto;



public record SuccessResponse(String status, ResponseData data) implements ApiResponse {
    public SuccessResponse(ResponseData data){
        this("success", data);
    }
}
