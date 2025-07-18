package com.cv_portal_backend.service;

import com.cv_portal_backend.model.CandidateCV;
import com.cv_portal_backend.repository.CandidateCVRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CVService {

    @Autowired
    private CandidateCVRepository repository;

    private final S3Service s3Service;

    public CVService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    private final Tika tika = new Tika();

    public String processAndStoreCV(MultipartFile file) {
        try {
            String content = tika.parseToString(file.getInputStream());

            String name = extractName(content);
            String email = extractEmail(content);
            String phone = extractPhone(content);
            String skills = extractSkills(content);

            // ✅ Generate unique file key
            String uniqueFileName = s3Service.generateUniqueFileName(file.getOriginalFilename());

            // ✅ Upload using unique filename
            String s3Url = s3Service.uploadFileWithKey(file, uniqueFileName);

            // ✅ Save actual key used in S3
            CandidateCV candidate = new CandidateCV();
            candidate.setFullName(name);
            candidate.setEmail(email);
            candidate.setPhone(phone);
            candidate.setSkills(skills);
            candidate.setFileName(uniqueFileName); // ✅ Use S3 key here
            candidate.setS3Url(s3Url);
            candidate.setUploadedAt(LocalDateTime.now());

            repository.save(candidate);

            return "CV uploaded and processed successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing CV: " + e.getMessage();
        }
    }

    public List<CandidateCV> getAllCVs() {
        return repository.findAll();
    }

    // ✅ Generate signed URL using exact S3 key
    public String generateSignedUrl(String fileName) {
        return s3Service.generatePreSignedUrl(fileName);
    }

    // ========= Utility Extractors =========

    private String extractName(String content) {
        content = content.replaceAll("[\\t\\r]+", "").trim();
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;
            if (line.toLowerCase().contains("curriculum vitae") ||
                    line.toLowerCase().contains("resume") ||
                    line.toLowerCase().contains("profile") ||
                    line.toLowerCase().contains("summary")) {
                continue;
            }
            if (line.matches("([A-Z][a-z]+\\s){1,3}[A-Z][a-z]+")) {
                return line;
            }
        }
        return "Unknown Name";
    }

    private String extractEmail(String content) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}");
        Matcher matcher = emailPattern.matcher(content);
        return matcher.find() ? matcher.group() : "unknown@example.com";
    }

    private String extractPhone(String content) {
        String phoneRegex = "(\\+91[-\\s]?)?[0]?[6789]\\d{9}";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group() : "0000000000";
    }

    public String extractSkills(String content) {
        content = content.toLowerCase();
        Set<String> knownSkills = new HashSet<>(Arrays.asList(
                "java", "python", "c++", "c#", "spring", "spring boot", "hibernate", "sql", "mysql",
                "postgresql", "mongodb", "aws", "azure", "gcp", "docker", "kubernetes", "git",
                "jenkins", "rest", "graphql", "javascript", "typescript", "react", "angular", "node.js",
                "html", "css", "bash", "shell", "linux", "jira"));

        Set<String> matchedSkills = new HashSet<>();
        for (String skill : knownSkills) {
            if (content.contains(skill)) {
                matchedSkills.add(skill);
            }
        }
        return matchedSkills.isEmpty() ? "Not Found" : String.join(", ", matchedSkills);
    }
}
