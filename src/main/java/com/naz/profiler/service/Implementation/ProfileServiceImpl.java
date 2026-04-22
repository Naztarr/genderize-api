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
        Sort sort = Sort.by("desc".equalsIgnoreCase(filter.getOrder())?
                Sort.Direction.DESC : Sort.Direction.ASC,
                filter.getSortBy() == null? "createdAt" : filter.getSortBy());

        Pageable pageable = PageRequest.of(filter.getPage()-1,
                Math.min(filter.getLimit(), 50), sort);

        Page<Profile> pageResult = repository
                .findAll(ProfileSpecification.filter(filter), pageable);

        return ResponseEntity.ok(new PageProfileResponse(filter.getPage(),
                filter.getLimit(), pageResult.getTotalElements(),
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
}
