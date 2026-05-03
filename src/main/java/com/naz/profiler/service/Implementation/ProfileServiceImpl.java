package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.*;
import com.naz.profiler.entity.Profile;
import com.naz.profiler.provider.ExternalService;
import com.naz.profiler.repository.ProfileRepository;
import com.naz.profiler.service.ProfileService;
import com.naz.profiler.spec.ProfileSpecification;
import com.naz.profiler.util.NaturalLanguageParser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final NaturalLanguageParser parser;
    private final ExternalService externalService;


    @Override
    public ResponseEntity<ApiResponse> createProfile(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Name is required"));
        }


        Optional<Profile> existing = repository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return ResponseEntity.ok(
                    new IdempotencyResponse(mapToResponse(existing.get()))
            );
        }


        GenderizeResponse gender = (GenderizeResponse) externalService.callGenderize(name);
        System.out.println(gender.getGender());
        AgifyResponse age = (AgifyResponse) externalService.callAgify(name);
        System.out.println(age.getAge());
        NationalizeResponse nationality = (NationalizeResponse) externalService.callNationalize(name);
        System.out.println(nationality.getCountry());


        if (gender.getGender() == null || gender.getCount() == 0) {
            return ResponseEntity.status(502).body(new ErrorResponse("Genderize returned an invalid response"));
        }

        if (age.getAge() == null) {
            return ResponseEntity.status(502).body(new ErrorResponse("Agify returned an invalid response"));
        }

        if (nationality.getCountry().isEmpty()) {
            return ResponseEntity.status(502).body(new ErrorResponse("Nationalize returned an invalid response"));
        }

        var topCountry = nationality.getCountry().stream()
                .max(Comparator.comparing(CountryData::probability))
                .orElseThrow();

        String ageGroup = classifyAge(age.getAge());

        Profile profile = new Profile();
        profile.setName(name);
        profile.setGender(gender.getGender());
        profile.setGenderProbability(gender.getProbability());
        profile.setAge(age.getAge());
        profile.setAgeGroup(ageGroup);
        profile.setCountryId(topCountry.countryId());
        profile.setCountryName(resolveName(topCountry.countryId()));
        profile.setCountryProbability(topCountry.probability());

        repository.saveAndFlush(profile);


        return ResponseEntity.status(201).body(new ProfileSuccessResp(mapToResponse(profile)));
    }

    private static String classifyAge(int age) {
        if (age <= 12) return "child";
        if (age <= 19) return "teenager";
        if (age <= 59) return "adult";
        return "senior";
    }
    private static ProfileResponseData mapToResponse(Profile p) {
        return new ProfileResponseData(
                p.getId(),
                p.getName(),
                p.getGender(),
                p.getGenderProbability(),
                p.getAge(),
                p.getAgeGroup(),
                p.getCountryId(),
                p.getCountryName(),
                p.getCountryProbability(),
                p.getCreatedAt()
        );
    }

    public static String resolveName(String countryId) {
        if (countryId == null || countryId.isBlank()) return "Unknown";

        // Create a locale using the ID
        Locale locale = new Locale("", countryId.toUpperCase());

        // Get the display name in English
        String name = locale.getDisplayCountry(Locale.ENGLISH);

        // If the ID was invalid, the name will be empty or equal the ID
        return name.isEmpty() ? countryId : name;
    }

    @Override
    public ResponseEntity<ApiResponse> getProfile(UUID id) {

        return repository.findById(id)
                .<ResponseEntity<ApiResponse>>map(profile ->
                        ResponseEntity.ok(
                                new ProfileSuccessResp(mapToResponse(profile))
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse("Profile not found"))
                );
    }

    @Override
    public ResponseEntity<ApiResponse> getProfiles(ProfileFilterRequest filter) {

        int page = (filter.getPage() == null || filter.getPage() < 1) ? 1 : filter.getPage();
        int limit = (filter.getLimit() == null || filter.getLimit() < 1) ? 10 : Math.min(filter.getLimit(), 50);

        Sort sort = Sort.by("desc".equalsIgnoreCase(filter.getOrder())?
                Sort.Direction.DESC : Sort.Direction.ASC,
                filter.getSortBy() == null? "createdAt" : mapSortField(filter.getSortBy()));

        Pageable pageable = PageRequest.of(page-1,
                Math.min(limit, 50), sort);

        Page<Profile> pageResult = repository
                .findAll(ProfileSpecification.filter(filter), pageable);

        int totalPages = (int)Math.ceil((double)pageResult.getTotalElements() / limit);

        Map<String, String> links = new HashMap<>();
        links.put("self", "/api/profiles?page="+page+"&limit="+limit);
        links.put("next", page < totalPages? "/api/profiles?page="+(page+1)+"&limit="+limit : null);
        links.put("prev", page > 1? "/api/profiles?page="+(page-1)+"&limit="+limit : null);

        return ResponseEntity.ok(new PageProfileResponse(page,
                limit, pageResult.getTotalElements(), totalPages, links,
                pageResult.stream().map(this::mapToProfileResponseData).toList()));
    }

    @Override
    public ResponseEntity<ApiResponse> search(String words, Integer page, Integer limit) {
        if (words == null || words.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Missing or empty parameter"));
        }

        ProfileFilterRequest filter = parser.parse(words);
        filter.setPage(page == null? 1 : page);
        filter.setLimit(limit == null? 10 : limit);

        return getProfiles(filter);

    }

    private ProfileResponseData mapToProfileResponseData(Profile profile){
        return new ProfileResponseData(profile.getId(), profile.getName(),
                profile.getGender(), profile.getGenderProbability(), profile.getAge(),
                profile.getAgeGroup(), profile.getCountryId(), profile.getCountryName(),
                profile.getCountryProbability(), profile.getCreatedAt());
    }


    private String mapSortField(String sortBy) {
        if (sortBy == null) return "createdAt";
        return switch (sortBy.toLowerCase()) {
            case "age" -> "age";
            case "gender_probability" -> "genderProbability";
            case "country_probability" -> "countryProbability";
            case "created_at" -> "createdAt";
            case "age_group" -> "ageGroup";
            case "country_id" -> "countryId";
            default -> "createdAt";
        };
    }

    public void exportCsv(ProfileFilterRequest filter, HttpServletResponse response) throws Exception{
        List<Profile> rows =
                repository.findAll(ProfileSpecification.filter(filter));

        String timestamp = Instant.now()
                .toString()
                .replace(":", "-");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"profiles_" +
                        timestamp +
                        ".csv\""
        );

        PrintWriter writer = response.getWriter();

        writer.println(
                "id,name,gender,gender_probability,age,age_group,country_id,country_name,country_probability,created_at"
        );

        for (Profile p : rows) {
            writer.println(
                    p.getId() + "," +
                            p.getName() + "," +
                            p.getGender() + "," +
                            p.getGenderProbability() + "," +
                            p.getAge() + "," +
                            p.getAgeGroup() + "," +
                            p.getCountryId() + "," +
                            p.getCountryName() + "," +
                            p.getCountryProbability() + "," +
                            p.getCreatedAt()
            );
        }

        writer.flush();
    }
}
