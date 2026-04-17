package com.naz.profiler.service;

import com.naz.profiler.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ClassifyService {

    public ResponseEntity<ApiResponse> classify(String name);
}
