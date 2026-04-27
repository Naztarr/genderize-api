package com.naz.profiler.entity;

import com.naz.profiler.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(unique = true)
    private String githubId;

    private String username;
    private String email;
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isActive = true;

    private Instant lastLoginAt;

    @CreationTimestamp
    private Instant createdAt;
}
