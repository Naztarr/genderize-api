package com.naz.profiler.service;

import com.naz.profiler.dto.CsvUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CsvUploadService {
    CsvUploadResponse upload(MultipartFile file) throws Exception;
}
