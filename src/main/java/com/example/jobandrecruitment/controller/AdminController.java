package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.UserResponse;
import com.example.jobandrecruitment.service.JobService;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;

    public AdminController(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiDataResponse<List<UserResponse>>> listUsers(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(required = false) String keyword) {
        List<UserResponse> users = userService.listUsers(page, size, keyword);
        ApiDataResponse<List<UserResponse>> body = ApiDataResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Users retrieved")
                .data(users)
                .errors(null)
                .httpStatus(org.springframework.http.HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/jobs/{jobId}/approve")
    public ResponseEntity<ApiDataResponse<Object>> approveJob(@PathVariable Long jobId) {
        jobService.approveJob(jobId);
        ApiDataResponse<Object> body = ApiDataResponse.builder()
                .success(true)
                .message("Job approved")
                .data(null)
                .errors(null)
                .httpStatus(org.springframework.http.HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }
}

