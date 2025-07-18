package com.cv_portal_backend.controller;

import com.cv_portal_backend.model.CandidateCV;
import com.cv_portal_backend.service.CVService;
import com.cv_portal_backend.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/cv")
public class CVUploadController {

    private final CVService cvService;
    private final S3Service s3Service;

    public CVUploadController(CVService cvService, S3Service s3Service) {
        this.cvService = cvService;
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file selected.");
            }

            String result = cvService.processAndStoreCV(file);
            if (result == null || result.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing failed.");
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CandidateCV>> getAllCVs() {
        List<CandidateCV> allCvs = cvService.getAllCVs();
        return ResponseEntity.ok(allCvs);
    }

    // ✅ Corrected endpoint to match frontend path: "/signed-url/{fileName}"
    @GetMapping("/signed-url/{fileName}")
    public ResponseEntity<String> getSignedUrl(@PathVariable String fileName) {
        try {
            String url = s3Service.generatePreSignedUrl(fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate signed URL: " + e.getMessage());
        }
    }
}
