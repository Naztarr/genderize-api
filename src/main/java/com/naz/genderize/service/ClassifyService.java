package com.naz.genderize.service;

import com.naz.genderize.dto.ApiResponse;
import com.naz.genderize.dto.SuccessResponse;
import org.springframework.http.ResponseEntity;

public interface ClassifyService {

    public ResponseEntity<ApiResponse> classify(String name);
}
