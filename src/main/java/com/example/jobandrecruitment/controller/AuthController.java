package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.AuthRequest;
import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.model.dto.response.AuthResponse;
import com.example.jobandrecruitment.security.JwtService;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    //    đăng ký tài khoản (EMPLOYER, CANDIDATE, ADMIN)
    @PostMapping("/register")
    public ResponseEntity<ApiDataResponse<String>> register(@RequestBody AuthRegisterRequest request) {
        userService.registerUser(request);

        ApiDataResponse<String> response = new ApiDataResponse<>();
        response.setSuccess(true);
        response.setMessage("Đăng ký tài khoản thành công");
        response.setData(null);
        response.setHttpStatus(HttpStatus.valueOf("CREATED"));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse(token);

        ApiDataResponse<AuthResponse> response = new ApiDataResponse<>();
        response.setSuccess(true);
        response.setMessage("Đăng nhập thành công");
        response.setData(authResponse);
        response.setHttpStatus(HttpStatus.OK);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

