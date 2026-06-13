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
    private String salary;
    private String requiredSkills;
    private LocalDateTime deadline;
    private boolean active;
    private String employerEmail;
    private LocalDateTime createdAt;

    public JobResponse(Long id, String title, String description, String location,
                       boolean active, String employerEmail, LocalDateTime createdAt) {
        this(id, title, description, location, null, null, null, active, employerEmail, createdAt);
    }
}

