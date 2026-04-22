package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SeedProfile{
    private String name;
    private String gender;

    @JsonProperty("gender_probability")
    private Double genderProbability;

    private Integer age;

    @JsonProperty("age_group")
    private String ageGroup;

    @JsonProperty("country_id")
    private String countryId;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("country_probability")
    private Double countryProbability;
}
