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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CsvUploadServiceImpl implements CsvUploadService {
    private static final int BATCH_SIZE = 1000;

    private final JdbcTemplate jdbcTemplate;

    private final ProfileRepository repository;

    @Async
    @CacheEvict(value = "profiles", allEntries = true)
    @Override
    public CsvUploadResponse upload(MultipartFile file) throws Exception {

        int totalRows = 0;
        int inserted = 0;
        int skipped = 0;

        Map<String, Integer> reasons = new HashMap<>();

        List<Profile> batch = new ArrayList<>();

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
                                    isBlank(countryId)
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

                    gender = gender.trim().toLowerCase();

                    if (
                            !gender.equals("male") &&
                                    !gender.equals("female")
                    ) {
                        skipped++;
                        increment(reasons, "invalid_gender");
                        continue;
                    }

                    boolean exists =
                            repository.findByNameIgnoreCase(name).isPresent();

                    if (exists) {
                        skipped++;
                        increment(reasons, "duplicate_name");
                        continue;
                    }

                    Double genderProbability;
                    Double countryProbability;

                    try {

                        genderProbability =
                                Double.parseDouble(genderProbabilityValue);

                        countryProbability =
                                Double.parseDouble(countryProbabilityValue);

                    } catch (Exception ex) {

                        skipped++;
                        increment(reasons, "invalid_probability");
                        continue;
                    }

                    Profile profile = new Profile();

                    profile.setName(name);
                    profile.setGender(gender);
                    profile.setAge(age);
                    profile.setAgeGroup(ageGroup);
                    profile.setCountryId(countryId.toUpperCase());
                    profile.setCountryName(countryName);

                    profile.setGenderProbability(genderProbability);
                    profile.setCountryProbability(countryProbability);

                    batch.add(profile);

                    if (batch.size() >= BATCH_SIZE) {

                        batchInsert(batch);

                        inserted += batch.size();

                        batch.clear();
                    }

                } catch (Exception ex) {

                    skipped++;
                    increment(reasons, "malformed_row");
                }
            }

            if (!batch.isEmpty()) {

                batchInsert(batch);

                inserted += batch.size();
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

    private void batchInsert(List<Profile> batch) {

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
                    country_probability,
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                sql,
                batch,
                batch.size(),
                (PreparedStatement ps, Profile p) -> {


                    ps.setString(1, p.getName());

                    ps.setString(2, p.getGender());

                    ps.setDouble(3, p.getGenderProbability());

                    ps.setInt(4, p.getAge());

                    ps.setString(5, p.getAgeGroup());

                    ps.setString(6, p.getCountryId());

                    ps.setString(7, p.getCountryName());

                    ps.setDouble(8, p.getCountryProbability());
                }
        );
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
