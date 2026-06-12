package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ForgotPasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ResetPasswordRequest;
import com.example.jobandrecruitment.model.dto.response.PasswordResetResponse;
import com.example.jobandrecruitment.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    void registerUser(AuthRegisterRequest request);

    List<UserResponse> listUsers(int page, int size, String keyword);

    void changePassword(ChangePasswordRequest request);

    void updateUser(Long id, com.example.jobandrecruitment.model.dto.request.UserRequest request);

    void deleteUser(Long id);

    PasswordResetResponse forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}

