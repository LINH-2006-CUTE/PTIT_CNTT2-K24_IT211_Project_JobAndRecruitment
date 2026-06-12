package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.request.AuthRequest;
import com.example.jobandrecruitment.model.dto.request.AuthRegisterRequest;
import com.example.jobandrecruitment.model.dto.request.RefreshTokenRequest;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    //    đăng ký tài khoản (EMPLOYER, CANDIDATE)
    @PostMapping("/register")
    public ResponseEntity<ApiDataResponse<String>> register(@RequestBody AuthRegisterRequest request) {
        userService.registerUser(request);

        ApiDataResponse<String> response = new ApiDataResponse<>();
        response.setSuccess(true);
        response.setMessage("Đăng ký tài khoản thành công");
        response.setData(null);
        response.setHttpStatus(HttpStatus.CREATED);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
        ApiDataResponse<AuthResponse> response = new ApiDataResponse<>();
        response.setSuccess(true);
        response.setMessage("Đăng nhập thành công");
        response.setData(authResponse);
        response.setHttpStatus(HttpStatus.OK);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiDataResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtService.isTokenExpired(refreshToken)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                AuthResponse authResponse = new AuthResponse(newAccessToken, refreshToken);

                ApiDataResponse<AuthResponse> response = new ApiDataResponse<>();
                response.setSuccess(true);
                response.setMessage("Access token được cấp lại thành công");
                response.setData(authResponse);
                response.setHttpStatus(HttpStatus.OK);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ApiDataResponse<AuthResponse> response = new ApiDataResponse<>();
                response.setSuccess(false);
                response.setMessage("Refresh token đã hết hạn");
                response.setData(null);
                response.setHttpStatus(HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            ApiDataResponse<AuthResponse> response = new ApiDataResponse<>();
            response.setSuccess(false);
            response.setMessage("Refresh token không hợp lệ");
            response.setData(null);
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
