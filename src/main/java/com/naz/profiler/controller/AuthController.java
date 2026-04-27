package com.naz.profiler.controller;

import com.naz.profiler.dto.AuthResponse;
import com.naz.profiler.security.GithubOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${github.client.id}")
    private String clientId;

    private final GithubOAuthService githubOAuthService;

    @GetMapping("/github")
    public void redirect(HttpServletResponse response)
            throws Exception {

        String url =
                "https://github.com/login/oauth/authorize" +
                        "?client_id=" + clientId +
                        "&scope=read:user user:email";

        response.sendRedirect(url);
    }

    @GetMapping("/github/callback")
    public ResponseEntity<AuthResponse> callback(
            @RequestParam String code
    ) {
        return ResponseEntity.ok(
                githubOAuthService.login(code)
        );
    }
}
