package com.naz.profiler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CsvUploadResponse {
    private String status;

    private int totalRows;

    private int inserted;

    private int skipped;

    private Map<String, Integer> reasons;
}
