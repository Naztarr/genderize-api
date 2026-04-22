package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record ProfileResponseData(
        UUID id,
        String name,
        String gender,
        @JsonProperty("gender_probability")
        double genderProbability,
        Integer age,
        @JsonProperty("age_group")
        String ageGroup,
        @JsonProperty("country_id")
        String countryId,
        @JsonProperty("country_name")
        String countryName,
        @JsonProperty("country_probability")
        double countryProbability,
        @JsonProperty("created_at")
        Instant createdAt
) {
}
