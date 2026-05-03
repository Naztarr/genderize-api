package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.UserResponse;
import com.naz.profiler.entity.User;
import com.naz.profiler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public ResponseEntity<UserResponse> getAuthenticatedUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) auth.getPrincipal();
            // Map the Entity to UserResponse DTO to keep the response clean
            return ResponseEntity.ok(new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatarUrl(),
                    user.getRole().name(),
                    user.getLastLoginAt()
            ));
    }
}
