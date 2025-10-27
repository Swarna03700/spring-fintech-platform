package com.fintech.wallet.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
	
	// This is the unique identifier from Auth0 (the 'sub' claim in the JWT)
	@Id
	@Column(name = "auth0_id", nullable = false, unique = true)
	private String auth0Id;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(name = "first_name", nullable = true)
	private String firstName;
	
	@Column(name = "last_name", nullable = true)
	private String lastName;
	
	@Column(name = "date_of_birth", nullable = true)
	private LocalDate dateOfBirth;
	
	@Column(name = "is_profile_complete", nullable = false)
	private boolean isProfileComplete;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	public UserEntity() {}
	
	public UserEntity(String auth0Id, String email, boolean isProfileComplete) {
		this.auth0Id = auth0Id;
		this.email = email;
		this.isProfileComplete = isProfileComplete;
	}
	
	public UserEntity(String auth0Id, String email, boolean isProfileComplete, String firstName, String lastName) {
		this(auth0Id, email, isProfileComplete);
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getAuth0Id() {
		return this.auth0Id;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public LocalDate getDateOfBirth() {
		return this.dateOfBirth;
	}
	
	public boolean getIsProfileComplete() {
		return this.isProfileComplete;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public void setIsProfileComplete(boolean isProfileComplete) {
		this.isProfileComplete = isProfileComplete;
	}
}
