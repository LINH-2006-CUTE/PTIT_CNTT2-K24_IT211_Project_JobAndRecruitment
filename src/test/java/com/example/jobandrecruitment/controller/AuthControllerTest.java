package com.example.jobandrecruitment.controller;
import com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ForgotPasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ResetPasswordRequest;
import com.example.jobandrecruitment.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController - Unit Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void should_ReturnHttpStatus200Ok_When_ValidChangePasswordData() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword123",
                "newPassword456",
                "newPassword456"
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Đổi mật khẩu thành công")))
                .andExpect(jsonPath("$.httpStatus", is("OK")));

        verify(userService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }


    @Test
    void should_ReturnHttpStatus200Ok_When_EmailExistsInForgotPassword() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("test@example.com");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Đã gửi email chứa mã xác thực")))
                .andExpect(jsonPath("$.httpStatus", is("OK")));

        verify(userService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void should_ReturnHttpStatus200Ok_When_ValidResetPasswordData() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "valid-reset-token-123",
                "newPassword789",
                "newPassword789"
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Đổi mật khẩu thành công")))
                .andExpect(jsonPath("$.httpStatus", is("OK")));

        verify(userService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void should_ReturnHttpStatus201Created_When_RegisterSuccess() throws Exception {
        com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest req = new com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest("new@example.com", "P@ssw0rd", "New User", com.example.jobandrecruitment.model.entity.RoleUser.CANDIDATE, null);
        String json = objectMapper.writeValueAsString(req);
        doNothing().when(userService).registerUser(any());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Đăng ký tài khoản thành công")));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void should_ReturnBadRequest_When_ChangePasswordThrowsAppException() throws Exception {
        com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest request = new com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest("old", "new", "new");
        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new com.example.jobandrecruitment.exception.AppException("Mật khẩu cũ không đúng")).when(userService).changePassword(any());

        mockMvc.perform(post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Mật khẩu cũ không đúng")));
    }

}

