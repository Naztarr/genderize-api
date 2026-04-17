package com.naz.profiler.controller;

import com.naz.profiler.dto.ApiResponse;
import com.naz.profiler.service.ClassifyService;
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
