package com.example.jobandrecruitment.advice;

import com.example.jobandrecruitment.exception.AppException;
import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class APIAdvice {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiDataResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message(ex.getMessage())
				.data(null)
				.errors(null)
				.httpStatus(HttpStatus.NOT_FOUND)
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ApiDataResponse<Object>> handleAppException(AppException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message(ex.getMessage())
				.data(null)
				.errors(null)
				.httpStatus(status)
				.build();
		return ResponseEntity.status(status).body(body);
	}

	// Handle BadCredentialsException (wrong password)
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiDataResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message("Email hoặc mật khẩu không đúng")
				.data(null)
				.errors(null)
				.httpStatus(HttpStatus.UNAUTHORIZED)
				.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

	// Handle UsernameNotFoundException (email not found)
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiDataResponse<Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message("Tài khoản không tồn tại")
				.data(null)
				.errors(null)
				.httpStatus(HttpStatus.UNAUTHORIZED)
				.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiDataResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			errors.put(fe.getField(), fe.getDefaultMessage());
		}
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message("Validation failed")
				.data(null)
				.errors(errors)
				.httpStatus(HttpStatus.BAD_REQUEST)
				.build();
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiDataResponse<Object>> handleAny(Exception ex) {
		ApiDataResponse<Object> body = ApiDataResponse.builder()
				.success(false)
				.message(ex.getMessage())
				.data(null)
				.errors(null)
				.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}