package com.naz.profiler.controller;

import com.naz.profiler.dto.UserResponse;
import com.naz.profiler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        System.out.println(authentication != null ? authentication.getPrincipal().toString() : "NO AUTH");
        // authentication.getName() usually returns the 'sub' (subject) from JWT
        return userService.getAuthenticatedUser(authentication);
    }
}
