package com.example.jobandrecruitment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    @NotBlank(message = "Mã liên kết hoặc đường dẫn file CV không được để trống")
    private String submittedCvUrl;
}