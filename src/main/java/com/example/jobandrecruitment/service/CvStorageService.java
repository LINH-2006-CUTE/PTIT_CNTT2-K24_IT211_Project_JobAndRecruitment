package com.example.jobandrecruitment.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.jobandrecruitment.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class CvStorageService {

    private final String uploadDir = Paths.get(".").toAbsolutePath().normalize() + "/uploads/";
    private final Cloudinary cloudinary;

    public CvStorageService(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudinary = hasCloudinaryConfig(cloudName, apiKey, apiSecret)
                ? new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret))
                : null;
    }

    public String savePdfCv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException("Please choose a PDF file");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new AppException("Only PDF CV files are accepted");
        }

        if (cloudinary != null) {
            return uploadToCloudinary(file);
        }

        return saveToLocalDisk(file);
    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            String publicId = "cv_" + UUID.randomUUID();
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "job-and-recruitment/cv",
                            "public_id", publicId,
                            "resource_type", "raw"
                    )
            );
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new AppException("Cloudinary did not return a secure URL", 503);
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new AppException("Cannot read CV file: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new AppException("Cannot upload CV to Cloudinary: " + e.getMessage(), 503);
        }
    }

    private String saveToLocalDisk(MultipartFile file) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new AppException("Cannot create upload directory");
            }

            String originalName = file.getOriginalFilename() == null ? "cv.pdf" : file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalName;
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new AppException("Cannot save CV file: " + e.getMessage());
        }
    }

    private boolean hasCloudinaryConfig(String cloudName, String apiKey, String apiSecret) {
        return !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
    }
}
