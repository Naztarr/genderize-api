package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ProfileList(
        UUID id,
        String name,
        String gender,
        Integer age,
        @JsonProperty("age_group")
        String ageGroup,
        @JsonProperty("country_id")
        String countryId
) {
}
