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
public class JobApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String coverLetter;
    private JobStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private String submittedCvUrl;
    private String employerFeedback;

    public JobApplicationResponse(Long id, Long jobId, String jobTitle, Long candidateId,
                                  String candidateName, String candidateEmail, String coverLetter,
                                  JobStatus status, LocalDateTime appliedAt, LocalDateTime updatedAt,
                                  String submittedCvUrl) {
        this(id, jobId, jobTitle, candidateId, candidateName, candidateEmail, coverLetter,
                status, appliedAt, updatedAt, submittedCvUrl, null);
    }
}

