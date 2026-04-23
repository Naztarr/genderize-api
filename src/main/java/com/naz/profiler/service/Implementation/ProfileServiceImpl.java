package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.*;
import com.naz.profiler.entity.Profile;
import com.naz.profiler.repository.ProfileRepository;
import com.naz.profiler.service.ProfileService;
import com.naz.profiler.spec.ProfileSpecification;
import com.naz.profiler.util.NaturalLanguageParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final NaturalLanguageParser parser;


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

        return ResponseEntity.ok(new PageProfileResponse(page,
                limit, pageResult.getTotalElements(),
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









//    private String mapSortField(String sortBy) {
//        if (sortBy == null || sortBy.isBlank()) return "createdAt";
//
//        return switch (sortBy) {
//            case "age" -> "age";
//            case "created_at" -> "createdAt";
//            case "gender_probability" -> "genderProbability";
//            default -> "createdAt";
//        };
//    }
}
