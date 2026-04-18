package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.*;
import com.naz.profiler.entity.Profile;
import com.naz.profiler.provider.ExternalService;
import com.naz.profiler.repository.ProfileRepository;
import com.naz.profiler.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final ExternalService externalService;

    @Override
    public ResponseEntity<ApiResponse> createProfile(ProfileRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Name is required"));
        }


        Optional<Profile> existing = repository.findByNameIgnoreCase(request.name());
        if (existing.isPresent()) {
            return ResponseEntity.ok(
                    new IdempotencyResponse(mapToResponse(existing.get()))
            );
        }


        GenderizeResponse gender = (GenderizeResponse) externalService.callGenderize(request.name());
        System.out.println(gender.getGender());
        AgifyResponse age = (AgifyResponse) externalService.callAgify(request.name());
        System.out.println(age.getAge());
        NationalizeResponse nationality = (NationalizeResponse) externalService.callNationalize(request.name());
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
        profile.setName(request.name());
        profile.setGender(gender.getGender());
        profile.setGenderProbability(gender.getProbability());
        profile.setSampleSize(gender.getCount());
        profile.setAge(age.getAge());
        profile.setAgeGroup(ageGroup);
        profile.setCountryId(topCountry.countryId());
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
                p.getSampleSize(),
                p.getAge(),
                p.getAgeGroup(),
                p.getCountryId(),
                p.getCountryProbability(),
                p.getCreatedAt()
        );
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
    public ResponseEntity<ApiResponse> getProfiles(String gender, String countryId, String ageGroup) {

        Specification<Profile> spec = (root, query, cb) -> cb.conjunction();

        if (gender != null && !gender.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("gender")), gender.toLowerCase().trim()));
        }

        if (countryId != null && !countryId.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("countryId")), countryId.toLowerCase().trim()));
        }

        if (ageGroup != null && !ageGroup.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("ageGroup")), ageGroup.toLowerCase().trim()));
        }

        // 2. Fetch from DB using the Spec
        List<Profile> filteredProfiles = repository.findAll(spec);

        List<ProfileList> list = filteredProfiles.stream().map(
                p -> new ProfileList(p.getId(), p.getName(),
                            p.getGender(), p.getAge(),
                            p.getAgeGroup(), p.getCountryId())
        ).toList();

        return ResponseEntity.ok(new ProfileListResponse(list.size(), list));
    }

    @Override
    public ResponseEntity<ApiResponse> deleteProfile(UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Profile not found"));
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}