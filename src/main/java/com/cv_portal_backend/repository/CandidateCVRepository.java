package com.cv_portal_backend.repository;

import com.cv_portal_backend.model.CandidateCV;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateCVRepository extends JpaRepository<CandidateCV, Long> {
}
