package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.request.ChangePasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ForgotPasswordRequest;
import com.example.jobandrecruitment.model.dto.request.ResetPasswordRequest;
import com.example.jobandrecruitment.model.dto.response.PasswordResetResponse;
import com.example.jobandrecruitment.model.dto.response.UserResponse;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, ResetTokenInfo> resetTokenStore = new HashMap<>();

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // Lấy user hiện tại đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException("Mật khẩu cũ không đúng");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới có trùng không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Mật khẩu mới và xác nhận mật khẩu không trùng nhau");
        }

        // Kiểm tra mật khẩu mới không được giống mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException("Mật khẩu mới không được giống mật khẩu cũ");
        }

        // Mã hoá mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public PasswordResetResponse forgotPassword(ForgotPasswordRequest request) {
        // Kiểm tra email có tồn tại không
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        // Tạo mã reset token (UUID)
        String resetToken = UUID.randomUUID().toString();

        // Lưu vào store tạm thời với thời gian hết hạn 15 phút
        long expirationTime = System.currentTimeMillis() + (15 * 60 * 1000); // 15 phút
        resetTokenStore.put(resetToken, new ResetTokenInfo(user.getId(), expirationTime));

        // Giả lập gửi email (trong thực tế sẽ gửi email)
        System.out.println("=== GIẢ LẬP GỬI EMAIL ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Reset Password");
        System.out.println("Body: Mã reset password của bạn: " + resetToken);
        System.out.println("Hết hạn trong: 15 phút");
        System.out.println("======================");

        return new PasswordResetResponse(resetToken, 15, "Email chứa mã reset password đã được gửi");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // Kiểm tra reset token có hợp lệ không
        ResetTokenInfo tokenInfo = resetTokenStore.get(request.getResetToken());
        if (tokenInfo == null) {
            throw new AppException("Mã reset không hợp lệ");
        }

        // Kiểm tra token có hết hạn không
        if (System.currentTimeMillis() > tokenInfo.getExpirationTime()) {
            resetTokenStore.remove(request.getResetToken());
            throw new AppException("Mã reset đã hết hạn");
        }

        // Kiểm tra mật khẩu mới và xác nhận
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Mật khẩu mới và xác nhận mật khẩu không trùng nhau");
        }

        // Lấy user từ tokenInfo
        User user = userRepository.findById(tokenInfo.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa token sau khi sử dụng
        resetTokenStore.remove(request.getResetToken());
    }

    @Override
    public void registerUser(AuthRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại");
        }
        
        User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName()).role(request.getRole()).companyName(request.getCompanyName()).isActive(true).build();
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, com.example.jobandrecruitment.model.dto.request.UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getCompanyName() != null) user.setCompanyName(request.getCompanyName());
        if (request.getIsActive() != null) user.setActive(request.getIsActive());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        // Soft delete: set isActive = false
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> listUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        var userPage = userRepository.findByFullNameContainingIgnoreCase(keyword == null ? "" : keyword, pageable);
        return userPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
    }

    private UserResponse toDto(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole());
    }

    private static class ResetTokenInfo {
        private final Long userId;
        private final long expirationTime;

        public ResetTokenInfo(Long userId, long expirationTime) {
            this.userId = userId;
            this.expirationTime = expirationTime;
        }

        public Long getUserId() {
            return userId;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }
}



