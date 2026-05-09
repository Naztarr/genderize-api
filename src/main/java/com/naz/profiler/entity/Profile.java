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

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "gender_Probability")
    private Double genderProbability;

    @Column(name = "age")
    private Integer age;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_Probability")
    private Double countryProbability;

    @CreationTimestamp
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;
}