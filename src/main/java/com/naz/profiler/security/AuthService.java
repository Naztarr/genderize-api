package com.naz.profiler.security;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.RefreshRequest;
import com.naz.profiler.dto.Response;
import com.naz.profiler.entity.RefreshToken;
import com.naz.profiler.entity.User;
import com.naz.profiler.exception.RefreshTokenException;
import com.naz.profiler.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtService jwtService;

    public ResponseEntity<ApiResponse> refresh(RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new RefreshTokenException("Invalid refresh token"));

        if (refreshToken.getRevoked()) {
            throw new RefreshTokenException("Token revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);

        String accessToken =
                jwtService.generateAccessToken(user);

        String newRefreshToken =
                UUID.randomUUID().toString();

        refreshTokenRepo.save(
                RefreshToken.builder()
                        .token(newRefreshToken)
                        .user(user)
                        .expiresAt(
                                Instant.now().plus(5, ChronoUnit.MINUTES))
                        .revoked(false)
                        .build()
        );
        return ResponseEntity.ok(new Response("success", accessToken, newRefreshToken));

    }

    public ResponseEntity<ApiResponse> logout(RefreshRequest request){
        refreshTokenRepo.findByToken(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                });

        return ResponseEntity.ok(new Response("success", "Logged out"));

    }
}
