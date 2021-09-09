package com.example.azureblobstoragespring.service;

import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.AccessTier;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobRequestConditions;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.example.azureblobstoragespring.payload.UploadFileResponse;
import com.example.azureblobstoragespring.property.FileLoggerProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

@Service
public class AzureBlobStorageService implements FileStorageService {

    @Override
    public UploadFileResponse storeFile(MultipartFile file) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        String containerName = "spring-blob";
        BlobContainerClient containerClient = createBlobContainerClient(blobServiceClient, containerName);
        BlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        uploadFileToBlob(file, blobClient);
        String fileDownloadUri = getFileDownloadUri(blobServiceClient, containerName, fileName);
        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    private void uploadFileToBlob(MultipartFile file, BlobClient blobClient) {
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());

        Map<String, String> metadata = Collections.singletonMap("metadata", "value");

        BlobRequestConditions requestConditions = new BlobRequestConditions();
        Context context = new Context("key", "value");
        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions();
        var timeout = java.time.Duration.ofSeconds(60);
        try {
            blobClient.uploadWithResponse(file.getInputStream(), file.getSize(),
                    parallelTransferOptions, headers, metadata, AccessTier.HOT,
                    requestConditions, timeout, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BlobContainerClient createBlobContainerClient(BlobServiceClient blobServiceClient, String containerName) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }

    private BlobServiceClient getBlobServiceClient() {
        String connectStr = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        return new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
    }

    private String getFileDownloadUri(BlobServiceClient blobServiceClient, String containerName, String fileName) {
        return "https://" + blobServiceClient.getAccountName() +
                "." + "blob.core.windows.net/" + containerName +
                "/" + fileName;
    }

    @Override
    public byte[] loadFileAsResource(final FileLoggerProperties fileLoggerProperties) {
        URI downloadUri = getDownloadUri(fileLoggerProperties);

        return getDataFromDownloadUri(downloadUri);
    }

    private byte[] getDataFromDownloadUri(URI downloadUri) {
        byte[] data = null;

        try {
            InputStream in = downloadUri.toURL().openStream();
            data = in.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private URI getDownloadUri(FileLoggerProperties fileLoggerProperties) {
        URI downloadUri = null;
        try {
            downloadUri = new URI(fileLoggerProperties.getFileDownloadUri());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return downloadUri;
    }


}
