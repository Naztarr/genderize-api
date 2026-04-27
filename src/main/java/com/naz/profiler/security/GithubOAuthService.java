package com.naz.profiler.security;

import com.naz.profiler.dto.AuthResponse;
import com.naz.profiler.dto.GithubUserResponse;
import com.naz.profiler.entity.RefreshToken;
import com.naz.profiler.entity.User;
import com.naz.profiler.enums.Role;
import com.naz.profiler.repository.RefreshTokenRepository;
import com.naz.profiler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GithubOAuthService {
    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepo;

    public AuthResponse login(String code) {

        String githubAccessToken = exchangeCode(code);

        GithubUserResponse githubUser =
                fetchGithubUser(githubAccessToken);

        User user = userRepository
                .findByGithubId(githubUser.getId().toString())
                .orElseGet(() -> {
                    User u = new User();
                    u.setGithubId(githubUser.getId().toString());
                    u.setUsername(githubUser.getLogin());
                    u.setEmail(githubUser.getEmail());
                    u.setAvatarUrl(githubUser.getAvatarUrl());
                    u.setRole(Role.ANALYST);
                    u.setIsActive(true);
                    return userRepository.save(u);
                });

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String access = jwtService.generateAccessToken(user);
        String refresh = UUID.randomUUID().toString();

        refreshTokenRepo.save(
                RefreshToken.builder()
                        .token(refresh)
                        .user(user)
                        .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                        .revoked(false)
                        .build()
        );

        return AuthResponse.builder()
                .status("success")
                .accessToken(access)
                .refreshToken(refresh)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }


    private String exchangeCode(String code) {

        RestClient client = RestClient.create();

        Map response = client.post()
                .uri("https://github.com/login/oauth/access_token")
                .header("Accept", "application/json")
                .body(Map.of(
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "code", code,
                        "redirect_uri", redirectUri
                ))
                .retrieve()
                .body(Map.class);

        return response.get("access_token").toString();
    }

    private GithubUserResponse fetchGithubUser(String token) {

        RestClient client = RestClient.create();

        return client.get()
                .uri("https://api.github.com/user")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(GithubUserResponse.class);
    }
}
