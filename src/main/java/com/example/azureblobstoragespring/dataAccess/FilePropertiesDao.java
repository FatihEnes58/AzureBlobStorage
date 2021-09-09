package com.example.azureblobstoragespring.dataAccess;

import com.example.azureblobstoragespring.property.FileLoggerProperties;

public interface FilePropertiesDao {
    void add(FileLoggerProperties fileLoggerProperties);
    FileLoggerProperties getFilePropertiesById(int fileID);
}
