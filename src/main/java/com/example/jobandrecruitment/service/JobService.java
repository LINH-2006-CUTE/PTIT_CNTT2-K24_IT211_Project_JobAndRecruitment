package com.example.jobandrecruitment.service;

import com.example.jobandrecruitment.model.dto.request.JobPostRequest;
import com.example.jobandrecruitment.model.dto.response.JobResponse;

import java.util.List;

public interface JobService {
	JobResponse postJob(JobPostRequest request);

	void approveJob(Long jobId);

	List<JobResponse> getAllJobs();

	java.util.List<JobResponse> getJobs(int page, int size, Boolean isActive);

	void deleteJob(Long id);

	java.util.List<JobResponse> searchJobs(String title, String skill, String location, int page, int size);
}
