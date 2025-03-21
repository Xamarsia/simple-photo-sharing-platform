package com.xamarsia.simplephotosharingplatform.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;


@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
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
