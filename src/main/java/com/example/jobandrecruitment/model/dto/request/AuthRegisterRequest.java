package com.example.jobandrecruitment.model.dto.request;

import com.example.jobandrecruitment.model.entity.RoleUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private RoleUser role; // EMPLOYER, ADMIN, CANDIDATE
    private String companyName; // dành cho employer
}

