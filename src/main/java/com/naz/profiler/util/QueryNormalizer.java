package com.naz.profiler.util;

import com.naz.profiler.dto.ProfileFilterRequest;
import org.springframework.stereotype.Component;

@Component
public class QueryNormalizer {
    public String normalize(ProfileFilterRequest filter) {

        return String.format(
                "gender=%s|" +
                        "ageGroup=%s|" +
                        "country=%s|" +
                        "minAge=%s|" +
                        "maxAge=%s|" +
                        "minGenderProb=%s|" +
                        "minCountryProb=%s|" +
                        "sort=%s|" +
                        "order=%s|" +
                        "page=%s|" +
                        "limit=%s",

                normalizeValue(filter.getGender()),
                normalizeValue(filter.getAgeGroup()),
                normalizeValue(filter.getCountryId()),
                filter.getMinAge(),
                filter.getMaxAge(),
                filter.getMinGenderProbability(),
                filter.getMinCountryProbability(),
                normalizeValue(filter.getSortBy()),
                normalizeValue(filter.getOrder()),
                filter.getPage(),
                filter.getLimit()
        );
    }

    private String normalizeValue(String value) {
        return value == null
                ? "null"
                : value.trim().toLowerCase();
    }
}
