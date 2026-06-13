package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.JobApplicationRequest;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import com.example.jobandrecruitment.service.CvStorageService;
import com.example.jobandrecruitment.service.JobApplicationService;
import com.example.jobandrecruitment.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateController {

    private final JobApplicationService jobApplicationService;
    private final CvStorageService cvStorageService;
    private final UserService userService;

    public CandidateController(JobApplicationService jobApplicationService,
                               CvStorageService cvStorageService,
                               UserService userService) {
        this.jobApplicationService = jobApplicationService;
        this.cvStorageService = cvStorageService;
        this.userService = userService;
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiDataResponse<JobApplicationResponse>> applyJob(
            @Valid @RequestBody CandidateApplyRequest request) {
        JobApplicationRequest applicationRequest = new JobApplicationRequest(
                request.getCoverLetter(),
                request.getSubmittedCvUrl()
        );
        JobApplicationResponse response = jobApplicationService.applyJob(request.getJobId(), applicationRequest);

        ApiDataResponse<JobApplicationResponse> body = ApiDataResponse.<JobApplicationResponse>builder()
                .success(true)
                .message("Application submitted successfully")
                .data(response)
                .errors(null)
                .httpStatus(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiDataResponse<List<JobApplicationResponse>>> getMyApplications() {
        List<JobApplicationResponse> applications = jobApplicationService.getCurrentCandidateApplications();

        ApiDataResponse<List<JobApplicationResponse>> body = ApiDataResponse.<List<JobApplicationResponse>>builder()
                .success(true)
                .message("Candidate applications retrieved")
                .data(applications)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(body);
    }

    @PostMapping("/cv/upload")
    public ResponseEntity<ApiDataResponse<String>> uploadCv(@RequestParam("file") MultipartFile file) {
        String fileUrl = cvStorageService.savePdfCv(file);
        userService.updateCurrentUserCvUrl(fileUrl);

        ApiDataResponse<String> body = ApiDataResponse.<String>builder()
                .success(true)
                .message("CV uploaded successfully")
                .data(fileUrl)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(body);
    }

    @Getter
    @Setter
    public static class CandidateApplyRequest {
        @NotNull(message = "Job id is required")
        private Long jobId;

        @NotBlank(message = "Cover letter is required")
        private String coverLetter;

        @NotBlank(message = "CV url is required")
        private String submittedCvUrl;
    }
}
