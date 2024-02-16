package com.xamarsia.simplephotosharingplatform.s3;

import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
public class S3Buckets {
    private String posts;
    private String profiles;

    public String getPostsImages() {
        System.out.println("Posts: " + posts);
        return posts;
    }

    public String getProfilesImages() {
        System.out.println("Profiles: " + profiles);
        return profiles;
    }
}
