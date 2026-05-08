package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.CsvUploadResponse;
import com.naz.profiler.entity.Profile;
import com.naz.profiler.repository.ProfileRepository;
import com.naz.profiler.service.CsvUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CsvUploadServiceImpl implements CsvUploadService {
    private static final int BATCH_SIZE = 1000;

    private final JdbcTemplate jdbcTemplate;

    private final ProfileRepository repository;


    @CacheEvict(value = "profiles", allEntries = true)
    @Override
    public CsvUploadResponse upload(MultipartFile file) throws Exception {

        int totalRows = 0;
        int inserted = 0;
        int skipped = 0;

        Map<String, Integer> reasons = new HashMap<>();

        List<Profile> batch = new ArrayList<>();
        Set<String> batchNames = new HashSet<>();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(file.getInputStream()));

                CSVParser csvParser =
                        new CSVParser(
                                reader,
                                CSVFormat.DEFAULT
                                        .builder()
                                        .setHeader()
                                        .setSkipHeaderRecord(true)
                                        .build())
        ) {

            for (CSVRecord row : csvParser) {

                totalRows++;

                try {

                    String name = row.get("name");
                    String gender = row.get("gender");
                    String genderProbabilityValue = row.get("gender_probability");
                    String ageValue = row.get("age");
                    String ageGroup = row.get("age_group");
                    String countryId = row.get("country_id");
                    String countryName = row.get("country_name");
                    String countryProbabilityValue = row.get("country_probability");

                    if (
                            isBlank(name) ||
                                    isBlank(gender) ||
                                    isBlank(ageValue) ||
                                    isBlank(countryId) ||
                                    isBlank(ageGroup) ||
                                    isBlank(countryName) ||
                                    isBlank(genderProbabilityValue) ||
                                    isBlank(countryProbabilityValue)
                    ) {

                        skipped++;
                        increment(reasons, "missing_fields");
                        continue;
                    }

                    Integer age;

                    try {
                        age = Integer.parseInt(ageValue);
                    } catch (Exception ex) {
                        skipped++;
                        increment(reasons, "invalid_age");
                        continue;
                    }

                    if (age < 0) {
                        skipped++;
                        increment(reasons, "invalid_age");
                        continue;
                    }

                    //Validate gender value
                    gender = gender.trim().toLowerCase();

                    if (
                            !gender.equals("male") &&
                                    !gender.equals("female")
                    ) {
                        skipped++;
                        increment(reasons, "invalid_gender");
                        continue;
                    }

                    //Validate age group
                    ageGroup = ageGroup.trim().toLowerCase();
                    Set<String> validAgeGroups = Set.of(
                            "child",
                            "teenager",
                            "adult",
                            "senior"
                    );

                    if (!validAgeGroups.contains(ageGroup)) {

                        skipped++;
                        increment(reasons, "invalid_age_group");

                        continue;
                    }

                    Double genderProbability;
                    Double countryProbability;

                    try {

                        genderProbability =
                                Double.parseDouble(genderProbabilityValue);

                        countryProbability =
                                Double.parseDouble(countryProbabilityValue);
                        if (
                                genderProbability < 0 ||
                                        genderProbability > 1 ||
                                        countryProbability < 0 ||
                                        countryProbability > 1
                        ) {
                            skipped++;
                            increment(reasons, "invalid_probability");
                            continue;
                        }

                    } catch (Exception ex) {

                        skipped++;
                        increment(reasons, "invalid_probability");
                        continue;
                    }

                    Profile profile = new Profile();

                    profile.setName(name.trim());
                    profile.setGender(gender.trim());
                    profile.setAge(age);
                    profile.setAgeGroup(ageGroup.trim());
                    profile.setCountryId(countryId.toUpperCase().trim());
                    profile.setCountryName(countryName.trim());

                    profile.setGenderProbability(genderProbability);
                    profile.setCountryProbability(countryProbability);


                    String normalizedName =
                            profile.getName().trim().toLowerCase();

                    if (batchNames.contains(normalizedName)) {

                        skipped++;
                        increment(reasons, "duplicate_name");

                        continue;
                    }

                    batchNames.add(normalizedName);

                    batch.add(profile);

                    if (batch.size() >= BATCH_SIZE) {

                        inserted += batchInsert(batch, reasons);
                        batch.clear();
                        batchNames.clear();
                    }

                } catch (Exception ex) {

                    skipped++;
                    increment(reasons, "malformed_row");
                }
            }

            if (!batch.isEmpty()) {

                inserted += batchInsert(batch, reasons);
                batch.clear();
                batchNames.clear();
            }
        }

        return new CsvUploadResponse(
                "success",
                totalRows,
                inserted,
                skipped,
                reasons
        );
    }

    private int batchInsert(
            List<Profile> batch,
            Map<String, Integer> reasons
    ) {

        Set<String> names =
                batch.stream()
                        .map(p -> p.getName().trim().toLowerCase())
                        .collect(Collectors.toSet());

        List<Profile> existingProfiles =
                repository.findExistingNames(names);

        Set<String> existingNames =
                existingProfiles.stream()
                        .map(p -> p.getName().trim().toLowerCase())
                        .collect(Collectors.toSet());

        List<Profile> filteredBatch =
                batch.stream()
                        .filter(p ->
                                !existingNames.contains(
                                        p.getName()
                                                .trim()
                                                .toLowerCase()))
                        .toList();

        int duplicates =
                batch.size() - filteredBatch.size();

        if (duplicates > 0) {

            reasons.put(
                    "duplicate_name",
                    reasons.getOrDefault(
                            "duplicate_name",
                            0
                    ) + duplicates
            );
        }

        if (filteredBatch.isEmpty()) {
            return 0;
        }

        String sql = """
            INSERT INTO profiles
            (
                name,
                gender,
                gender_probability,
                age,
                age_group,
                country_id,
                country_name,
                country_probability
            )
            VALUES
            (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (name) DO NOTHING
            """;

        jdbcTemplate.batchUpdate(
                sql,
                filteredBatch,
                filteredBatch.size(),
                (PreparedStatement ps, Profile p) -> {

                    ps.setString(1, p.getName());

                    ps.setString(2, p.getGender());

                    ps.setDouble(
                            3,
                            p.getGenderProbability()
                    );

                    ps.setInt(4, p.getAge());

                    ps.setString(5, p.getAgeGroup());

                    ps.setString(6, p.getCountryId());

                    ps.setString(7, p.getCountryName());

                    ps.setDouble(
                            8,
                            p.getCountryProbability()
                    );
                }
        );

        return filteredBatch.size();
    }

    private boolean isBlank(String value) {

        return value == null || value.trim().isEmpty();
    }

    private void increment(
            Map<String, Integer> reasons,
            String key
    ) {

        reasons.put(
                key,
                reasons.getOrDefault(key, 0) + 1
        );
    }
}
