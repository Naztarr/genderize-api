package com.naz.genderize.dto;


public record ErrorResponse(String status, String message) implements ApiResponse {
    public ErrorResponse(String message) {
        this("error", message);}
}
