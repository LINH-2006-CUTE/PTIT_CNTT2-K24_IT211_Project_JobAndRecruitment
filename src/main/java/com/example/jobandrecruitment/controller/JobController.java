package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.request.JobPostRequest;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.model.dto.response.JobResponse;
import com.example.jobandrecruitment.service.JobApplicationService;
import com.example.jobandrecruitment.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;
    private final JobApplicationService jobApplicationService;

    public JobController(JobService jobService, JobApplicationService jobApplicationService) {
        this.jobService = jobService;
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiDataResponse<JobResponse>> postJob(@Valid @RequestBody JobPostRequest request) {
        JobResponse resp = jobService.postJob(request);
        ApiDataResponse<JobResponse> body = ApiDataResponse.<JobResponse>builder()
                .success(true)
                .message("Đã đăng Job")
                .data(resp)
                .errors(null)
                .httpStatus(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(201).body(body);
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<ApiDataResponse<JobApplicationResponse>> applyJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobApplicationRequest request) {

        JobApplicationResponse response = jobApplicationService.applyJob(jobId, request);

        ApiDataResponse<JobApplicationResponse> body = ApiDataResponse.<JobApplicationResponse>builder()
                .success(true)
                .message("Nộp hồ sơ thành công")
                .data(response)
                .errors(null)
                .httpStatus(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(201).body(body);
    }
}

