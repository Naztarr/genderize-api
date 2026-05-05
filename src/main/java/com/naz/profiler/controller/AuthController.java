package com.naz.profiler.controller;

import com.naz.profiler.dto.*;
import com.naz.profiler.security.AuthService;
import com.naz.profiler.security.GithubOAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.cli.client.id}")
    private String cliClientId;

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
    public void callback(
            @RequestParam String code, HttpServletResponse response
    ) throws IOException {
//        AuthResponse authData = githubOAuthService.login(code);
//
//        addAuthCookies(response, authData.getAccessToken(), authData.getRefreshToken());
//        response.sendRedirect("http://localhost:3000/dashboard");
        try {
            AuthResponse authData = githubOAuthService.login(code);
            addAuthCookies(response, authData.getAccessToken(), authData.getRefreshToken());
            response.sendRedirect("http://localhost:3000/dashboard");
        } catch (Exception e) {
            // This will print the FULL error and line number in your Railway logs
            e.printStackTrace();

            // Send the error message to the browser so you can see it
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    @PostMapping("/cli/exchange")
    public ResponseEntity<AuthResponse> exchangeCli(@RequestBody CliExchangeRequest request) {
        return githubOAuthService.exchangeCodeWithPkce(request.getCode(), request.getVerifier());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(
            @RequestBody RefreshRequest request
    ) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            HttpServletRequest request, HttpServletResponse response
    ) {
        String refreshToken = extractRefreshTokenFromCookies(request);
        clearAuthCookies(response);
        if (refreshToken != null) {
            return authService.logout(new RefreshRequest(refreshToken));
        }

        return ResponseEntity.ok(new ErrorResponse("Already logged out"));
    }

    private void addAuthCookies(HttpServletResponse response, String access, String refresh) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", access)
                .httpOnly(true)
                .secure(true) // Set to false only for local localhost testing without SSL
                .path("/")
                .maxAge(180)
                .sameSite("None")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refresh)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(300)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .maxAge(0)
                .path("/")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
