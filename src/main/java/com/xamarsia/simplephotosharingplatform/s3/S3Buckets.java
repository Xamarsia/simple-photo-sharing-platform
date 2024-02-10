package com.xamarsia.simplephotosharingplatform.s3;

import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets.posts")
public class S3Buckets {
    private String images;

    public String getImages() {
        System.out.println("Images: " + images);
        return images;
    }
}
