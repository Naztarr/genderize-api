package com.naz.genderize.service.Implementation;

import com.naz.genderize.dto.*;
import com.naz.genderize.service.ClassifyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

@Service
public class ClassifyServiceImplementation implements ClassifyService {
    private final RestTemplate restTemplate;
    private final String genderizeUrl = "https://api.genderize.io";

    public ClassifyServiceImplementation(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<ApiResponse> classify(String name){
        if(name == null || name.trim().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Name is required"));
        }
        if(!name.matches("^[A-Za-z]+$")){
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse("Invalid name format"));
        }


        GenderizeResponse response;
        try{
            response = restTemplate.getForObject(
                    UriComponentsBuilder
                            .fromUriString(genderizeUrl)
                            .queryParam("name", name)
                            .toUriString(),
                    GenderizeResponse.class
            );
        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse("External API error"));
        }

        if(response == null ||
                response.getGender() == null ||
                response.getCount() == null ||
                response.getCount() == 0){
            return ResponseEntity.ok(
                    new ErrorResponse("No prediction available for the provided name")
            );
        }

        boolean isConfident = response.getProbability() >= 0.7 && response.getCount() >= 100;

        ResponseData data = new ResponseData(
                response.getName(),
                response.getGender(),
                response.getProbability(),
                response.getCount(),
                isConfident,
                Instant.now().toString()
        );

        return ResponseEntity.ok(new SuccessResponse(data));


    }
}

