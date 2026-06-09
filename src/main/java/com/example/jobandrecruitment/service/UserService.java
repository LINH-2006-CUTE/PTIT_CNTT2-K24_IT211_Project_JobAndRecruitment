package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> listUsers(int page, int size, String keyword);
}

