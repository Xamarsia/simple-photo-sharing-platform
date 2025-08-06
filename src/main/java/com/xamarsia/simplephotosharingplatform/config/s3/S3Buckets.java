package com.xamarsia.simplephotosharingplatform.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;


@Setter
@Configuration
@ConfigurationProperties(prefix = "minio.buckets")
public class S3Buckets {
    private String posts;
    private String profiles;

    public String getPostsImages() {
        return posts;
    }

    public String getProfilesImages() {
        return profiles;
    }
}
