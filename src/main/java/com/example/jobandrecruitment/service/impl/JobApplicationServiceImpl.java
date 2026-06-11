package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.request.UpdateApplicationStatusRequest;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.model.entity.Job;
import com.example.jobandrecruitment.model.entity.JobApplication;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.JobApplicationRepository;
import com.example.jobandrecruitment.repository.JobRepository;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.JobApplicationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository,
                                   JobRepository jobRepository,
                                   UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JobApplicationResponse applyJob(Long jobId, JobApplicationRequest request) {
        // Get authenticated candidate
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated candidate not found");
        }

        String email = auth.getName();
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        // Check if job exists
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // Check if candidate already applied
        var existingApp = jobApplicationRepository.findByJobIdAndCandidateId(jobId, candidate.getId());
        if (existingApp.isPresent()) {
            throw new AppException("Bạn đã nộp hồ sơ cho vị trí này rồi");
        }

        // Create application
        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(request.getCoverLetter())
                .build();

        JobApplication saved = jobApplicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify that authenticated user is the employer of this job
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        String email = auth.getName();
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new AppException("Bạn không có quyền cập nhật trạng thái hồ sơ này");
        }

        // Update status
        application.setStatus(request.getStatus());
        JobApplication updated = jobApplicationRepository.save(application);

        return mapToResponse(updated);
    }

    @Override
    public List<JobApplicationResponse> getJobApplications(Long jobId) {
        // Get authenticated employer
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        String email = auth.getName();
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if job exists and belongs to employer
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new AppException("Bạn không có quyền xem hồ sơ cho vị trí này");
        }

        return jobApplicationRepository.findByJobId(jobId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationResponse> getCandidateApplications(Long candidateId) {
        return jobApplicationRepository.findByCandidateId(candidateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationResponse getApplicationById(Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return mapToResponse(application);
    }

    // Helper method to map entity to DTO
    private JobApplicationResponse mapToResponse(JobApplication app) {
        return new JobApplicationResponse(
                app.getId(),
                app.getJob().getId(),
                app.getJob().getTitle(),
                app.getCandidate().getId(),
                app.getCandidate().getFullName(),
                app.getCandidate().getEmail(),
                app.getCoverLetter(),
                app.getStatus(),
                app.getAppliedAt(),
                app.getUpdatedAt()
        );
    }
}

