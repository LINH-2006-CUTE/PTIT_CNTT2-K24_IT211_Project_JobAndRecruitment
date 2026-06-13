package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import com.example.jobandrecruitment.service.CvStorageService;
import com.example.jobandrecruitment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final CvStorageService cvStorageService;
    private final UserService userService;

    public FileController(CvStorageService cvStorageService, UserService userService) {
        this.cvStorageService = cvStorageService;
        this.userService = userService;
    }

    @PostMapping("/upload-cv")
    public ResponseEntity<ApiDataResponse<String>> uploadCv(@RequestParam("file") MultipartFile file) {
        String fileUrl = cvStorageService.savePdfCv(file);
        userService.updateCurrentUserCvUrl(fileUrl);

        ApiDataResponse<String> body = ApiDataResponse.<String>builder()
                .success(true)
                .message("CV uploaded successfully")
                .data(fileUrl)
                .errors(null)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(body);
    }
}
