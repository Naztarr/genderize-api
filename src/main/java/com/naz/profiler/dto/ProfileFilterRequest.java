package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFilterRequest{
private String gender;

@JsonProperty("age_group")
private String ageGroup;

@JsonProperty("country_id")
private String countryId;

@Min(0)
@JsonProperty("min_age")
private Integer minAge;

@Min(0)
@JsonProperty("max_age")
private Integer maxAge;

@Min(0)
@Max(1)
@JsonProperty("min_gender_probability")
private Double minGenderProbability;

@Min(0)
@Max(1)
@JsonProperty("min_country_probability")
private Double minCountryProbability;

@JsonProperty("sort_by")
private String sortBy;

private String order;

@Min(1)
private Integer page;

@Min(1)
@Max(50)
private Integer limit;
        }
