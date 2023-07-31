package com.firstspringmvcapp.services;

import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Map;

@Component
public class MinioFileService {

    private final MinioClient minioClient;

    @Autowired
    public MinioFileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void upload(String bucketName, String endFileName, MultipartFile file) {
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(endFileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .userMetadata(Map.of("Content-type", "image"))
                            .build());

            System.out.println("minio object was successfully uploaded to bucket!");
        } catch (MinioException | IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void remove(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (MinioException | IOException | GeneralSecurityException e) {
            System.out.println("Error happened when removing image!");
            e.printStackTrace();
        }
    }
}
