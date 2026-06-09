package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.model.dto.response.UserResponse;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}

