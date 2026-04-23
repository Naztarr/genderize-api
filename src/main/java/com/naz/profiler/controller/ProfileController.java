package com.naz.profiler.controller;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/profiles")
public class ProfileController {

    public final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@Valid @ModelAttribute ProfileFilterRequest request) {
        return service.getProfiles(request);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return service.search(q, page, limit);
    }
}
