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

    //
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

    // lấy thông tin cụ thể của một ứng dụng, bao gồm cả thông tin ứng viên và trạng thái hiện tại của ứng dụng
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

    // lấy thông tin chi tiết của một ứng dụng cụ thể, bao gồm cả thông tin ứng viên và trạng thái hiện tại của ứng dụng
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

