package com.naz.profiler.controller;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/profiles")
public class ProfileController {

    public final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<ApiResponse> getAll( @RequestParam(required = false) String gender,
                                               @RequestParam(name = "age_group", required = false) String ageGroup,
                                               @RequestParam(name = "country_id", required = false) String countryId,
                                               @RequestParam(name = "min_age", required = false) Integer minAge,
                                               @RequestParam(name = "max_age", required = false) Integer maxAge,
                                               @RequestParam(name = "min_gender_probability", required = false) Double minGenderProbability,
                                               @RequestParam(name = "min_country_probability", required = false) Double minCountryProbability,
                                               @RequestParam(name = "sort_by", required = false) String sortBy,
                                               @RequestParam(required = false) String order,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer limit) {
        ProfileFilterRequest request = ProfileFilterRequest.builder()
                .gender(gender).ageGroup(ageGroup).countryId(countryId).minAge(minAge).maxAge(maxAge)
                .minGenderProbability(minGenderProbability).minCountryProbability(minCountryProbability).sortBy(sortBy)
                .order(order).page(page).limit(limit).build();

        System.out.println(request.getGender() + request.getCountryId() + request.getMinGenderProbability()+request.getMinAge());
        return service.getProfiles(request);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return service.search(q, page, limit);
    }
}
