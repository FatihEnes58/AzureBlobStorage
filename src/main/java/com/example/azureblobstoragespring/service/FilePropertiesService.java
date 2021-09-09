package com.example.azureblobstoragespring.service;

import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;

public interface FilePropertiesService {
    void add(UploadFileResponse uploadFileResponse);
    FileLoggerProperties getFilePropertiesById(int fileID);
}
