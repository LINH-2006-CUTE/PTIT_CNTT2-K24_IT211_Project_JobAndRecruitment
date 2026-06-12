package com.example.jobandrecruitment.repository;

import com.example.jobandrecruitment.model.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

	Page<Job> findByIsActive(boolean isActive, Pageable pageable);

	// Tìm kiếm chuẩn từ dưới database, kết hợp phân trang chính xác
	@Query("SELECT j FROM Job j WHERE j.isActive = true " +
			"AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
			"AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
			"AND (:skill IS NULL OR LOWER(j.description) LIKE LOWER(CONCAT('%', :skill, '%')))")
	Page<Job> searchActiveJobs(@Param("title") String title,
	                           @Param("location") String location,
	                           @Param("skill") String skill,
	                           Pageable pageable);
}
