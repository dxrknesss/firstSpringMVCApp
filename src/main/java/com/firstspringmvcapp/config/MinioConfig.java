package com.firstspringmvcapp.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${access_key}")
    private String accessKey;
    @Value("${secret_key}")
    private String secretKey;
    @Value("${minio_url}")
    private String minioUrl;

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .credentials(accessKey, secretKey)
                .endpoint(minioUrl)
                .build();
    }
}
