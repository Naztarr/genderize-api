package com.naz.profiler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NationalizeResponse {
    private Integer count;
    private List<CountryData> country;
}
