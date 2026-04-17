package com.naz.profiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseData(
        String name,
        String gender,
        Double probability,

        @JsonProperty("sample_size")
        Integer sampleSize,

        @JsonProperty("is_confident")
        boolean isConfident,

        @JsonProperty("processed_at")
        String processedAt
) {
}
