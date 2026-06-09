package com.example.jobandrecruitment.repository;

import com.example.jobandrecruitment.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
