package com.naz.profiler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"),
        indexes = {

                @Index(
                        name = "idx_country_gender_age",
                        columnList = "countryId, gender, age"
                ),

                @Index(
                        name = "idx_age_group",
                        columnList = "ageGroup"
                ),

                @Index(
                        name = "idx_created_at",
                        columnList = "createdAt"
                ),

                @Index(
                        name = "idx_gender_probability",
                        columnList = "genderProbability"
                ),

                @Index(
                        name = "idx_country_probability",
                        columnList = "countryProbability"
                )
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    private String name;
    private String gender;
    private Double genderProbability;
    private Integer age;
    private String ageGroup;
    private String countryId;
    private String countryName;
    private Double countryProbability;

    @CreationTimestamp
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;
}