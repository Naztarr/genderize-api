package com.naz.profiler.provider;

import com.naz.profiler.dto.AgifyResponse;
import com.naz.profiler.dto.ErrorResponse;
import com.naz.profiler.dto.GenderizeResponse;
import com.naz.profiler.dto.NationalizeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExternalService {
    private final RestTemplate restTemplate;
    private final String genderizeUrl = "https://api.genderize.io";
    private final String agifyUrl = "https://api.agify.io";
    private final String nationalizeUrl = "https://api.nationalize.io";

    public ExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object callGenderize(String name){
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
        return response;
    }

    public Object callAgify(String name){
        AgifyResponse response;
        try{
            response = restTemplate.getForObject(
                    UriComponentsBuilder
                            .fromUriString(agifyUrl)
                            .queryParam("name", name)
                            .toUriString(),
                    AgifyResponse.class
            );
        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse("External API error"));
        }
        return response;
    }

    public Object callNationalize(String name){
        NationalizeResponse response;
        try{
            response = restTemplate.getForObject(
                    UriComponentsBuilder
                            .fromUriString(genderizeUrl)
                            .queryParam("name", name)
                            .toUriString(),
                    NationalizeResponse.class
            );
        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse("External API error"));
        }
        return response;
    }
}
