package com.example.jobandrecruitment.service.impl;

import com.example.jobandrecruitment.exception.ResourceNotFoundException;
import com.example.jobandrecruitment.model.dto.request.JobPostRequest;
import com.example.jobandrecruitment.model.dto.response.JobResponse;
import com.example.jobandrecruitment.model.entity.Job;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.JobRepository;
import com.example.jobandrecruitment.repository.UserRepository;
import com.example.jobandrecruitment.service.JobService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JobResponse postJob(JobPostRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ResourceNotFoundException("Authenticated employer not found");
        }
        if (auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated employer not found");
        }
        String email = auth.getName();
        User employer = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .employer(employer)
                .isActive(false)
                .build();

        Job saved = jobRepository.save(job);

        return new JobResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getLocation(), saved.isActive(), saved.getEmployer().getEmail(), saved.getCreatedAt());
    }

    @Override
    public void approveJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng"));
        job.setActive(true);
        jobRepository.save(job);
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream().map(j -> new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getLocation(), j.isActive(), j.getEmployer() != null ? j.getEmployer().getEmail() : null, j.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public java.util.List<JobResponse> getJobs(int page, int size, Boolean isActive) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Job> pageResult;
        if (isActive == null) {
            pageResult = jobRepository.findAll(pageable);
        } else {
            pageResult = jobRepository.findByIsActive(isActive, pageable);
        }
        return pageResult.getContent().stream().map(j -> new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getLocation(), j.isActive(), j.getEmployer() != null ? j.getEmployer().getEmail() : null, j.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng"));
        job.setActive(false);
        jobRepository.save(job);
    }

    @Override
    public java.util.List<JobResponse> searchJobs(String title, String skill, String location, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        // Gọi câu Query từ Repository để DB tự lọc và phân trang chuẩn
        org.springframework.data.domain.Page<Job> pageResult = jobRepository.searchActiveJobs(title, location, skill, pageable);

        return pageResult.getContent().stream()
                .map(j -> new JobResponse(
                        j.getId(),
                        j.getTitle(),
                        j.getDescription(),
                        j.getLocation(),
                        j.isActive(),
                        j.getEmployer() != null ? j.getEmployer().getEmail() : null,
                        j.getCreatedAt()))
                .collect(Collectors.toList());
    }
}

