package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    // Register new user (EMPLOYER, CANDIDATE, ADMIN)
    void registerUser(AuthRegisterRequest request);

    // List all users (for Admin) with search & paging
    List<UserResponse> listUsers(int page, int size, String keyword);
}

