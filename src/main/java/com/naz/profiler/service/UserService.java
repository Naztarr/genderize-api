package com.naz.profiler.service;

import com.naz.profiler.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UserService {
    ResponseEntity<UserResponse> getAuthenticatedUser(Authentication auth);
}
