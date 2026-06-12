package com.example.jobandrecruitment.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    private String resetToken;
    private String newPassword;
    private String confirmPassword;
}

