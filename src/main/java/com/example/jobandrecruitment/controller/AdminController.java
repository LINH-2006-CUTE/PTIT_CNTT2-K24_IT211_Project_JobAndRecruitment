package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.UserResponse;
import com.example.jobandrecruitment.service.JobService;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import com.example.jobandrecruitment.model.dto.request.UserRequest;
import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/users")
    public ResponseEntity<ApiDataResponse<Object>> createUser(@RequestBody UserRequest request) {
        AuthRegisterRequest reg = new AuthRegisterRequest(request.getEmail(), request.getPassword(), request.getFullName(), request.getRole(), request.getCompanyName());
        userService.registerUser(reg);
        ApiDataResponse<Object> body = ApiDataResponse.builder()
                .success(true)
                .message("User created")
                .data(null)
                .errors(null)
                .httpStatus(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiDataResponse<Object>> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        userService.updateUser(id, request);
        ApiDataResponse<Object> body = ApiDataResponse.builder()
                .success(true)
                .message("User updated")
                .data(null)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiDataResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiDataResponse<Object> body = ApiDataResponse.builder()
                .success(true)
                .message("User deleted")
                .data(null)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiDataResponse<java.util.List<com.example.jobandrecruitment.model.dto.response.JobResponse>>> listJobs(@RequestParam(defaultValue = "0") int page,
                                                                                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                                                                                @RequestParam(required = false) Boolean isActive) {
        java.util.List<com.example.jobandrecruitment.model.dto.response.JobResponse> jobs = jobService.getJobs(page, size, isActive);
        ApiDataResponse<java.util.List<com.example.jobandrecruitment.model.dto.response.JobResponse>> body = ApiDataResponse.<java.util.List<com.example.jobandrecruitment.model.dto.response.JobResponse>>builder()
                .success(true)
                .message("Jobs retrieved")
                .data(jobs)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiDataResponse<Object>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        ApiDataResponse<Object> body = ApiDataResponse.builder()
                .success(true)
                .message("Job removed")
                .data(null)
                .errors(null)
                .httpStatus(HttpStatus.OK)
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

