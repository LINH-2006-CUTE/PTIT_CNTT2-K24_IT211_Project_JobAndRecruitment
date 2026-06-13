package com.example.jobandrecruitment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    private String salary;

    private String requiredSkills;

    private LocalDateTime deadline;

    public JobPostRequest(String title, String description, String location) {
        this.title = title;
        this.description = description;
        this.location = location;
    }
}

