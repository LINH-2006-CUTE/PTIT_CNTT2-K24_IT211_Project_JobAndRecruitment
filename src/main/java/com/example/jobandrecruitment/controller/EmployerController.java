package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.UpdateApplicationStatusRequest;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employer")
public class EmployerController {

    private final JobApplicationService jobApplicationService;

    public EmployerController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    /**
     * Employer: Get all applications for a job
     * GET /api/v1/employer/jobs/{jobId}/applications
     */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiDataResponse<List<JobApplicationResponse>>> getJobApplications(
            @PathVariable Long jobId) {
        List<JobApplicationResponse> applications = jobApplicationService.getJobApplications(jobId);

        ApiDataResponse<List<JobApplicationResponse>> body = ApiDataResponse.<List<JobApplicationResponse>>builder()
                .success(true)
                .message("Job applications retrieved")
                .data(applications)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Employer: Update application status (PENDING, REVIEWING, INTERVIEWING, ACCEPTED, REJECTED)
     * PUT /api/v1/employer/applications/{applicationId}/status
     */
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiDataResponse<JobApplicationResponse>> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        JobApplicationResponse updated = jobApplicationService.updateApplicationStatus(applicationId, request);

        ApiDataResponse<JobApplicationResponse> body = ApiDataResponse.<JobApplicationResponse>builder()
                .success(true)
                .message("Application status updated")
                .data(updated)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Employer: Get application details
     * GET /api/v1/employer/applications/{applicationId}
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiDataResponse<JobApplicationResponse>> getApplication(
            @PathVariable Long applicationId) {

        JobApplicationResponse application = jobApplicationService.getApplicationById(applicationId);

        ApiDataResponse<JobApplicationResponse> body = ApiDataResponse.<JobApplicationResponse>builder()
                .success(true)
                .message("Application retrieved")
                .data(application)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }
}

