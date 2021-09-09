package com.example.azureblobstoragespring.service;

import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadFileResponse storeFile(MultipartFile file);
    byte[] loadFileAsResource(final FileLoggerProperties fileLoggerProperties);
}
