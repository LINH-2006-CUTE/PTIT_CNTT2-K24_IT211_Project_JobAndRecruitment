package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.request.UpdateApplicationStatusRequest;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.model.entity.JobStatus;

import java.util.List;

public interface JobApplicationService {

    /**
     * Candidate apply for a job
     */
    JobApplicationResponse applyJob(Long jobId, JobApplicationRequest request);

    /**
     * Employer update application status (PENDING, REVIEWING, INTERVIEWING, ACCEPTED, REJECTED)
     */
    JobApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request);

    /**
     * Get all applications for a job (used by Employer)
     */
    List<JobApplicationResponse> getJobApplications(Long jobId);

    /**
     * Get all applications from a candidate
     */
    List<JobApplicationResponse> getCandidateApplications(Long candidateId);

    /**
     * Get single application
     */
    JobApplicationResponse getApplicationById(Long applicationId);
}

