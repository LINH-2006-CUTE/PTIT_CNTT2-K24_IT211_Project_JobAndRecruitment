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
public class UserRequest {
    private String email;
    private String password;
    private String fullName;
    private RoleUser role;
    private String companyName;
    private Boolean isActive;
}

