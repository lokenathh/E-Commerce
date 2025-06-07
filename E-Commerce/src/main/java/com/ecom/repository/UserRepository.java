package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.UserDetails;

public interface UserRepository extends JpaRepository<UserDetails, Integer> {
	public UserDetails findByEmail(String email);

	
	boolean existsByEmail(String email);

	public UserDetails findByResetToken(String token);

	
}
