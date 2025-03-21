package com.xamarsia.simplephotosharingplatform.s3;

import org.springframework.stereotype.Service;

import com.xamarsia.simplephotosharingplatform.exception.exceptions.AWSException;

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

    public void putObject(String bucketName, String key, byte[] file) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3.putObject(objectRequest, RequestBody.fromBytes(file));
        } catch (Exception e) {
            throw new AWSException("[S3Service]: putObject error with key " + key + "\n Error: " + e.getMessage());
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
            throw new AWSException("[S3Service]: getObject error with key " + key + "\n Error: " + e.getMessage());
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
            throw new AWSException("[S3Service]: deleteObject error with key " + key + "\n Error: " + e.getMessage());
        }
    }
}
