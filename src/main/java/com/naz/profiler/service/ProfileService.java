package com.naz.profiler.service;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.dto.ProfileRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


public interface ProfileService {

    public ResponseEntity<ApiResponse> createProfile(String name);

    public ResponseEntity<ApiResponse> getProfile(UUID id);

    ResponseEntity<ApiResponse> getProfiles(ProfileFilterRequest filter);
    ResponseEntity<ApiResponse> search(String words, Integer page, Integer limit);
    public void exportCsv(ProfileFilterRequest filter, HttpServletResponse response) throws Exception;

}
