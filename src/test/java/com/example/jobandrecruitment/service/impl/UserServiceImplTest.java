package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ForgotPasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ResetPasswordRequest;
import com.example.jobandrecruitment.model.dto.response.PasswordResetResponse;
import com.example.jobandrecruitment.model.entity.RoleUser;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword123")
                .fullName("Test User")
                .role(RoleUser.CANDIDATE)
                .isActive(true)
                .build();
    }

    // test1 : user đăng nhập mk cũ đu  ng, mk mới khác mk cũ, confirm password trùng new password => cập nhật mk thành công
    @Test
    void should_UpdatePassword_When_OldPasswordIsCorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword123",
                "newPassword456",
                "newPassword456"
        );

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("oldPassword123", "hashedPassword123")).thenReturn(true);
            when(passwordEncoder.encode("newPassword456")).thenReturn("hashedNewPassword456");

            assertDoesNotThrow(() -> userService.changePassword(request));

            assertEquals("hashedNewPassword456", testUser.getPassword());
            verify(userRepository, times(1)).save(testUser);
        }
    }

    // test2: user  gửi email tồn tại trong DB => trả về reset token và thời gian hết hạn
    @Test
    @DisplayName("TEST 2: should_GenerateResetToken_When_EmailExists")
    void should_GenerateResetToken_When_EmailExists() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        PasswordResetResponse response = userService.forgotPassword(request);
        assertNotNull(response);
        assertNotNull(response.getResetToken());
        assertEquals(15, response.getExpirationMinutes());
        assertEquals("Email chứa mã reset password đã được gửi", response.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }


    @Test
    @DisplayName("TEST 3: should_ResetPassword_When_ValidTokenAndPasswordsMatch")
    void should_ResetPassword_When_ValidTokenAndPasswordsMatch() {
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        PasswordResetResponse resetResponse = userService.forgotPassword(forgotRequest);
        String validToken = resetResponse.getResetToken();

        ResetPasswordRequest resetRequest = new ResetPasswordRequest(
                validToken,
                "newPassword789",
                "newPassword789"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword789")).thenReturn("hashedNewPassword789");
        assertDoesNotThrow(() -> userService.resetPassword(resetRequest));
        assertEquals("hashedNewPassword789", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

}

