package com.fintech.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
	Optional<UserEntity> findByEmail(String email);
	
	/**
	 * Find a user by their unique Auth0 subject identifier.
	 */
	Optional<UserEntity> findByAuth0Id(String auth0Id);
	
}
