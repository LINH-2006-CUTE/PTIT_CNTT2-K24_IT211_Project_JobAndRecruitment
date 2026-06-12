package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.request.UpdateApplicationStatusRequest;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.model.entity.JobStatus;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse applyJob(Long jobId, JobApplicationRequest request);

    JobApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request);

    List<JobApplicationResponse> getJobApplications(Long jobId);

    List<JobApplicationResponse> getCandidateApplications(Long candidateId);

    JobApplicationResponse getApplicationById(Long applicationId);
}

