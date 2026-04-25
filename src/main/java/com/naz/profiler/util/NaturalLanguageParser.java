package com.naz.profiler.util;

import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.exception.QueryInterpretationException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NaturalLanguageParser {
    public ProfileFilterRequest parse(String q) {

        if (q == null || q.isBlank()) {
            throw new QueryInterpretationException("Unable to interpret query");
        }

        String s = q.toLowerCase().trim();

        String gender = null;
        String ageGroup = null;
        String countryId = CountryResolver.resolveCode(s);

        Integer minAge = null;
        Integer maxAge = null;

        Double minGenderProbability = null;
        Double minCountryProbability = null;


        // Gender
        boolean hasMale = Pattern.compile("\\bmales?\\b").matcher(s).find();
        boolean hasFemale = Pattern.compile("\\bfemales?\\b").matcher(s).find();

        if (hasMale && hasFemale) {
            gender = null; // means both
        } else if (hasFemale) {
            gender = "female";
        } else if (hasMale) {
            gender = "male";
        }


        // Age Groups
        if (s.contains("child")) {
            ageGroup = "child";
        }

        if (s.contains("teenager")) {
            ageGroup = "teenager";
        }

        if (s.contains("adult")) {
            ageGroup = "adult";
        }

        if (s.contains("senior")) {
            ageGroup = "senior";
        }


        // Young => 16 - 24
        if (s.contains("young")) {
            minAge = 16;
            maxAge = 24;
        }


        Matcher aboveAge = Pattern.compile("(above|older than)\\s+(\\d+)").matcher(s);
        if (aboveAge.find()) {
            minAge = Integer.parseInt(aboveAge.group(2)) + 1;
        }

        Matcher belowAge = Pattern.compile("(below|under)\\s+(\\d+)").matcher(s);

        if (belowAge.find()) {
            maxAge = Integer.parseInt(belowAge.group(2)) -1;
        }


        // Gender Probability
        Matcher genderProb = Pattern.compile(
                "(gender\\s+(?:confidence|probability|score)).*?(?:above|over|min|minimum)?\\s*(0?\\.\\d+|\\d+)"
        ).matcher(s);

        if (genderProb.find()) {
            minGenderProbability =
                    Double.parseDouble(genderProb.group(2));
        }


        // Country Probability
        Matcher countryProb = Pattern.compile(
                "(country|nationality)\\s+(?:confidence|probability|score).*?(?:above|over|min|minimum)?\\s*(0?\\.\\d+|\\d+)"
        ).matcher(s);

        if (countryProb.find()) {
            minCountryProbability =
                    Double.parseDouble(countryProb.group(2));
        }

        // If Nothing Parsed
        boolean empty =
                gender == null &&
                        ageGroup == null &&
                        countryId == null &&
                        minAge == null &&
                        maxAge == null &&
                        minGenderProbability == null &&
                        minCountryProbability == null;

        if (empty) {
            throw new QueryInterpretationException("Unable to interpret query");
        }

        ProfileFilterRequest request = new ProfileFilterRequest();

        request.setGender(gender);
        request.setAgeGroup(ageGroup);
        request.setCountryId(countryId);

        request.setMinAge(minAge);
        request.setMaxAge(maxAge);

        request.setMinGenderProbability(minGenderProbability);
        request.setMinCountryProbability(minCountryProbability);

        request.setSortBy(null);
        request.setOrder(null);

        request.setPage(1);
        request.setLimit(10);

        return request;
    }
}
