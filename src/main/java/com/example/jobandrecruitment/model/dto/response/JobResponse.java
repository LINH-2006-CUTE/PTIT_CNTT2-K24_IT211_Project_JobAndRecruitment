package com.example.jobandrecruitment.model.dto.response;

import com.example.jobandrecruitment.model.entity.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private boolean active;
    private String employerEmail;
    private LocalDateTime createdAt;
}

