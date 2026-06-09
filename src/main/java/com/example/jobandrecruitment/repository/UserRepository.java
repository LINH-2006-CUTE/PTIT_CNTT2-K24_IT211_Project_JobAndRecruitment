package com.example.jobandrecruitment.repository;

import com.example.jobandrecruitment.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	// Support admin searching users by full name with paging
	Page<User> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
}
