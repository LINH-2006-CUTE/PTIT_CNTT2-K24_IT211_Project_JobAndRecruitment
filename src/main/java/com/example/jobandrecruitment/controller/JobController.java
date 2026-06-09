package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.JobPostRequest;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.JobResponse;
import com.example.jobandrecruitment.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiDataResponse<JobResponse>> postJob(@Valid @RequestBody JobPostRequest request) {
        JobResponse resp = jobService.postJob(request);
        ApiDataResponse<JobResponse> body = ApiDataResponse.<JobResponse>builder()
                .success(true)
                .message("Job posted")
                .data(resp)
                .errors(null)
                .httpStatus(org.springframework.http.HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(201).body(body);
    }
}

