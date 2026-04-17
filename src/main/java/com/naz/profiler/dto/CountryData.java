package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CountryData(
        @JsonProperty("country_id")
        String countryId,
        Double probability) {

}
