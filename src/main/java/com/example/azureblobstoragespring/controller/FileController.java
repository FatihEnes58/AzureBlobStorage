package com.example.azureblobstoragespring.controller;

import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;
import com.example.azureblobstoragespring.service.FilePropertiesService;
import com.example.azureblobstoragespring.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.tomcat.util.http.fileupload.util.Streams.DEFAULT_BUFFER_SIZE;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FilePropertiesService filePropertiesService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestPart(value = "file") MultipartFile file) {
        UploadFileResponse uploadFileResponse = fileStorageService.storeFile(file);
        filePropertiesService.add(uploadFileResponse);
        return uploadFileResponse;
    }

    @GetMapping("/downloadFile/{fileID:.+}")
    public @ResponseBody
    void getReviewedFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("fileID") int fileID) {

        FileLoggerProperties fileLoggerProperties = filePropertiesService.getFilePropertiesById(fileID);
        var resource = fileStorageService.loadFileAsResource(fileLoggerProperties);
        setResponseProperties(response, fileLoggerProperties, resource);
    }

    private void setResponseProperties(HttpServletResponse response, FileLoggerProperties fileLoggerProperties, byte[] resource) {
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(fileLoggerProperties.getFileType());
        try {
            response.getOutputStream().write(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
