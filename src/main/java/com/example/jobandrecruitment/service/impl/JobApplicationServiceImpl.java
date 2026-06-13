package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.request.UpdateApplicationStatusRequest;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.model.entity.Job;
import com.example.jobandrecruitment.model.entity.JobApplication;
import com.example.jobandrecruitment.model.entity.JobStatus;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.JobApplicationRepository;
import com.example.jobandrecruitment.repository.JobRepository;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.JobApplicationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        User candidate = getCurrentUser("Candidate not found");
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.isActive()) {
            throw new AppException("Job is not approved yet", 409);
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDateTime.now())) {
            throw new AppException("Job application deadline has passed", 409);
        }

        boolean alreadyApplied = jobApplicationRepository
                .findByJobIdAndCandidateId(jobId, candidate.getId())
                .isPresent();
        if (alreadyApplied) {
            throw new AppException("You already applied for this job", 409);
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(request.getCoverLetter())
                .submittedCvUrl(request.getSubmittedCvUrl())
                .status(JobStatus.PENDING)
                .build();

        return mapToResponse(jobApplicationRepository.save(application));
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        User employer = getCurrentUser("Employer not found");
        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new AppException("You cannot update an application for another employer's job", 403);
        }

        if (!application.getJob().isActive()) {
            throw new AppException("Job is not approved yet", 409);
        }

        application.setStatus(request.getStatus());
        application.setEmployerFeedback(request.getFeedback());

        return mapToResponse(jobApplicationRepository.save(application));
    }

    @Override
    public List<JobApplicationResponse> getJobApplications(Long jobId) {
        User employer = getCurrentUser("Employer not found");
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new AppException("You cannot view applications for another employer's job", 403);
        }

        return jobApplicationRepository.findByJobId(jobId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationResponse> getCurrentCandidateApplications() {
        User candidate = getCurrentUser("Candidate not found");
        return jobApplicationRepository.findByCandidateId(candidate.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationResponse getApplicationById(Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        User currentUser = getCurrentUser("User not found");
        boolean isJobOwner = application.getJob().getEmployer().getId().equals(currentUser.getId());
        boolean isCandidateOwner = application.getCandidate().getId().equals(currentUser.getId());

        if (!isJobOwner && !isCandidateOwner) {
            throw new AppException("You cannot view this application", 403);
        }

        return mapToResponse(application);
    }

    private User getCurrentUser(String notFoundMessage) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AppException("Authentication is required", 401);
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(notFoundMessage));
    }

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
                app.getUpdatedAt(),
                app.getSubmittedCvUrl(),
                app.getEmployerFeedback()
        );
    }
}
