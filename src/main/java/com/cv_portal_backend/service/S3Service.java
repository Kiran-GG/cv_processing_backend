package com.cv_portal_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    // ✅ Upload file with auto-generated unique name (default method)
    public String uploadFile(MultipartFile file) {
        String uniqueName = generateUniqueFileName(file.getOriginalFilename());
        return uploadFileWithKey(file, uniqueName);
    }

    // ✅ Upload file with custom S3 key (used by CVService)
    public String uploadFileWithKey(MultipartFile file, String key) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return generateS3FileUrl(key); // Optional: return public URL
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    // ✅ Generate unique filename – now public so CVService can use it
    public String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + Instant.now().getEpochSecond() + "_" + originalFilename;
    }

    // ✅ Generate a public static S3 file URL
    private String generateS3FileUrl(String filename) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + filename;
    }

    // ✅ Download file stream if needed (rarely used)
    public InputStream downloadFile(String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    // ✅ Main method: Generate secure pre-signed S3 URL (5 min validity)
    public String generatePreSignedUrl(String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(5))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
