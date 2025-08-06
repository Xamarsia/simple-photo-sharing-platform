package com.xamarsia.simplephotosharingplatform.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.AWSException;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ApplicationException;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@AllArgsConstructor
public class S3Service {
    private final S3Client s3;

    public void putObject(String bucketName, String key, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("[UploadImage]: File is empty. Cannot save an empty file");
        }
        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if (!(Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg "))) {
            throw new IllegalArgumentException(String.format("[UploadImage]: Wrong file extension '%s' found. "
                    + "Only .jpg and .jpeg files are allowed.", extension));
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        try {
            s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.AWS_S3_ERROR, "[UploadImage]: " + e.getMessage());
        } catch (Exception e) {
            throw new AWSException("[S3Service]: Put object error with key " + key + "\n Error: " + e.getMessage());
        }
    }

    public byte[] getObject(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);
            return res.readAllBytes();
        } catch (Exception e) {
            throw new AWSException("[S3Service]: Get object error with key " + key + "\n Error: " + e.getMessage());
        }
    }

    public void deleteObject(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new AWSException("[S3Service]: Delete object error with key " + key + "\n Error: " + e.getMessage());
        }
    }
}
