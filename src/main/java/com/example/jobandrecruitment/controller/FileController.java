package com.example.jobandrecruitment.controller;

import com.example.jobandrecruitment.model.dto.response.ApiDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    // Thư mục lưu trữ tạm thời ngay trong project
    private final String UPLOAD_DIR = Paths.get(".").toAbsolutePath().normalize().toString() + "/uploads/";

    @PostMapping("/upload-cv")
    public ResponseEntity<ApiDataResponse<String>> uploadCv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new com.example.jobandrecruitment.exception.AppException("Vui lòng chọn một file PDF để tải lên");
        }


        // Kiểm tra định dạng file bắt buộc phải là PDF theo FR-09
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new com.example.jobandrecruitment.exception.AppException("Hệ thống chỉ chấp nhận hồ sơ định dạng file PDF");
        }

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Tạo tên file ngẫu nhiên để tránh trùng lặp trùng tên trên hệ thống
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File dest = new File(UPLOAD_DIR + fileName);
            file.transferTo(dest);

            // Trả về đường dẫn để Candidate nộp vào API Apply Job
            String fileUrl = "/uploads/" + fileName;

            ApiDataResponse<String> body = ApiDataResponse.<String>builder().success(true).message("Tải lên hồ sơ CV thành công!").data(fileUrl).errors(null).httpStatus(HttpStatus.OK).build();

            return ResponseEntity.ok(body);

        } catch (IOException e) {
            throw new com.example.jobandrecruitment.exception.AppException("Lỗi trong quá trình lưu trữ file: " + e.getMessage());
        }
    }
}
