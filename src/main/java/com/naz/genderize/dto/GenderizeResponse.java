package com.naz.genderize.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenderizeResponse {
    private Integer count;
    private String name;
    private String gender;
    private Double probability;
}
