package com.cv_portal_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsRegionConfig {

    @Bean
    public Region awsRegion() {
        // This ensures the AWS SDK knows what region to use before any S3Client is
        // built
        return Region.AP_SOUTH_1; // or Region.of("ap-south-1")
    }
}
