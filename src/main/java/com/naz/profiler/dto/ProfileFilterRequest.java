package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileFilterRequest {
        private String gender;

        @JsonProperty("age_group")
        private String ageGroup;

        @JsonProperty("country_id")
        private String countryId;

        @JsonProperty("min_age")
        private Integer minAge;

        @JsonProperty("max_age")
        private Integer maxAge;

        @JsonProperty("min_gender_probability")
        private Double minGenderProbability;

        @JsonProperty("min_country_probability")
        private Double minCountryProbability;

        @JsonProperty("sort_by")
        private String sortBy;

        private String order;
        private Integer page;
        private Integer limit;
}
