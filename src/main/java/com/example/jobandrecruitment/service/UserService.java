package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    void registerUser(AuthRegisterRequest request);

    List<UserResponse> listUsers(int page, int size, String keyword);
}

