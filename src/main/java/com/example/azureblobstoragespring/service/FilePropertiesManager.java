package com.example.azureblobstoragespring.service;

import com.example.azureblobstoragespring.dataAccess.FilePropertiesDao;
import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FilePropertiesManager implements FilePropertiesService {

    private FilePropertiesDao filePropertiesDao;

    @Autowired
    public FilePropertiesManager(FilePropertiesDao filePropertiesDao){
        this.filePropertiesDao = filePropertiesDao;
    }

    @Override
    @Transactional
    public void add(UploadFileResponse uploadFileResponse) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        FileLoggerProperties fileLoggerProperties = new FileLoggerProperties();
        fileLoggerProperties.setFileName(uploadFileResponse.getFileName());
        fileLoggerProperties.setFileType(uploadFileResponse.getFileType());
        fileLoggerProperties.setFileDownloadUri(uploadFileResponse.getFileDownloadUri());
        fileLoggerProperties.setUploadDate(format.format(date));
        filePropertiesDao.add(fileLoggerProperties);
    }

    @Override
    public FileLoggerProperties getFilePropertiesById(int fileID) {
        return filePropertiesDao.getFilePropertiesById(fileID);
    }

}
