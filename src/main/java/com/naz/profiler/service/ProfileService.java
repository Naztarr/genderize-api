package com.naz.profiler.service;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileFilterRequest;
import org.springframework.http.ResponseEntity;


public interface ProfileService {

    ResponseEntity<ApiResponse> getProfiles(ProfileFilterRequest filter);
    ResponseEntity<ApiResponse> search(String words, Integer page, Integer limit);

}
