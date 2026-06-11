package com.example.jobandrecruitment.repository;

import com.example.jobandrecruitment.model.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobId(Long jobId);

    List<JobApplication> findByCandidateId(Long candidateId);

    Optional<JobApplication> findByJobIdAndCandidateId(Long jobId, Long candidateId);
}

