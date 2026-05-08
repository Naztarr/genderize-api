package com.naz.profiler.controller;

import com.naz.profiler.dto.*;
import com.naz.profiler.service.CsvUploadService;
import com.naz.profiler.service.ProfileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService service;
    private final CsvUploadService csvUploadService;

    public ProfileController(ProfileService service, CsvUploadService csvUploadService) {
        this.service = service;
        this.csvUploadService = csvUploadService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody String name) {
        return service.createProfile(name);
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable UUID id) {
        return service.getProfile(id);
    }


    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(required = false) String gender,
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

    @GetMapping("/profiles/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return service.search(q, page, limit);
    }

    @GetMapping("/profiles/export")
    public void exportCsv(@ModelAttribute ProfileFilterRequest filter, HttpServletResponse response) throws Exception {
        service.exportCsv(filter, response);
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvUploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        return ResponseEntity.ok(
                csvUploadService.upload(file)
        );
    }
}
