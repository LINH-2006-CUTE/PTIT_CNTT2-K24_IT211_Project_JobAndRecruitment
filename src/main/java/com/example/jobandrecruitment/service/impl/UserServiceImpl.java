package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.response.UserResponse;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(AuthRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại");
        }

        User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())) // Encode password using bcrypt
                .fullName(request.getFullName()).role(request.getRole()).companyName(request.getCompanyName()).isActive(true).build();

        userRepository.save(user);
    }

    //
    @Override
    public List<UserResponse> listUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        var userPage = userRepository.findByFullNameContainingIgnoreCase(keyword == null ? "" : keyword, pageable);
        return userPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
    }

    private UserResponse toDto(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole());
    }
}



