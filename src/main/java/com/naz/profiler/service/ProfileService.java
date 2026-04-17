package com.naz.profiler.service;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ProfileService {
    public ResponseEntity<ApiResponse> createProfile(ProfileRequest request);

    public ResponseEntity<ApiResponse> getProfile(UUID id);

    public ResponseEntity<ApiResponse> getProfiles(String gender, String countryId, String ageGroup);

    public ResponseEntity<ApiResponse> deleteProfile(UUID id);
}
