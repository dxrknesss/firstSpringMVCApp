package com.firstspringmvcapp.util;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Map;

public class MinioFileHandler {

    private static final MinioClient minioClient;

    static {
        minioClient = MinioClient
                .builder()
                .endpoint("http://minio-ser:9000")
                .credentials("mTc8tc1Bu2dw123D80FI", "ns21prsasZ5CGZjBdjvn4SnDydrNliThetoJHBDY")
                .build();
    }

    public static void upload(String bucketName, String endFileName, InputStream object) {
        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(endFileName)
                            .stream(object, object.available(), -1)
                            .userMetadata(Map.of("Content-type", "image"))
                            .build());

            System.out.println("minio object was successfully uploaded to bucket!");
        } catch (MinioException | IOException | GeneralSecurityException e) {
            System.out.println("Error happened when uploading image!");
            //e.printStackTrace();
        }
    }

    public static void remove(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (MinioException | IOException | GeneralSecurityException e) {
            System.out.println("Error happened when removing image!");
            // e.printStackTrace();
        }
    }

    public static boolean checkExistence(String bucketName, String objectName) {
        boolean result = false;
        try {
            minioClient.statObject(
                    StatObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            result = true;
        } catch (MinioException | IOException | GeneralSecurityException e) {
            System.out.println("Error happened when checking image!");
            // e.printStackTrace();
        }

        return result;
    }
}
