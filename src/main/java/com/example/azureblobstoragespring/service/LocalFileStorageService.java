package com.example.azureblobstoragespring.service;

import com.example.azureblobstoragespring.exception.FileStorageException;
import com.example.azureblobstoragespring.exception.MyFileNotFoundException;
import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;
import com.example.azureblobstoragespring.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public LocalFileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public UploadFileResponse storeFile(MultipartFile file) {

        String fileDownloadUri = getFileDownloadUri(file);
        return getUploadFileResponse(file, fileDownloadUri);
    }

    private String getFileDownloadUri(MultipartFile file) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(file.getOriginalFilename())
                .toUriString();
    }

    private UploadFileResponse getUploadFileResponse(MultipartFile file, String fileDownloadUri) {
        String fileName = file.getOriginalFilename();
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public byte[] loadFileAsResource(FileLoggerProperties fileLoggerProperties) {
        String fileName = fileLoggerProperties.getFileName();
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                try {
                    return resource.getInputStream().readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
        return null;
    }
}
