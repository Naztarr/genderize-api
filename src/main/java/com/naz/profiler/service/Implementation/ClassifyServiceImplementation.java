package com.naz.profiler.service.Implementation;

import com.naz.profiler.dto.*;
import com.naz.profiler.provider.ExternalService;
import com.naz.profiler.service.ClassifyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ClassifyServiceImplementation implements ClassifyService {
    private final ExternalService externalService;

    public ClassifyServiceImplementation(ExternalService externalService) {
        this.externalService = externalService;
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


        GenderizeResponse response = (GenderizeResponse) externalService.callGenderize(name);
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

