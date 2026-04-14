package com.naz.genderize.controller;

import com.naz.genderize.dto.ApiResponse;
import com.naz.genderize.service.ClassifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassifyController {
    public final ClassifyService service;

    public ClassifyController(ClassifyService service) {
        this.service = service;
    }

    @GetMapping("/classify")
    public ResponseEntity<ApiResponse> classify(@RequestParam(required = false) String name) {
        return service.classify(name);
    }
}
