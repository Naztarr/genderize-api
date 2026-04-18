package com.naz.profiler.controller;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileRequest;
import com.naz.profiler.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/profiles")
public class ProfileController {

    public final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody ProfileRequest request) {
        return service.createProfile(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable UUID id) {
        return service.getProfile(id);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String gender,
            @RequestParam(name = "country_id",required = false) String countryId,
            @RequestParam(name = "age_group",required = false) String ageGroup
    ) {
        return service.getProfiles(gender, countryId, ageGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        return service.deleteProfile(id);
    }
}
