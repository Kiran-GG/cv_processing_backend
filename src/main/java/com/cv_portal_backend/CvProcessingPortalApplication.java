package com.cv_portal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration.class
})
public class CvProcessingPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CvProcessingPortalApplication.class, args);
    }
}
