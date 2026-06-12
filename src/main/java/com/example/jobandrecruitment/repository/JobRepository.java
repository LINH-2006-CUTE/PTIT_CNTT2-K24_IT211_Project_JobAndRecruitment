package com.example.jobandrecruitment.repository;

import com.example.jobandrecruitment.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {

	Page<Job> findByIsActive(boolean isActive, Pageable pageable);

}
