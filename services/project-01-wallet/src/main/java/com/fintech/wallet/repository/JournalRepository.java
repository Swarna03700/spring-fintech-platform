package com.fintech.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.Journal;

public interface JournalRepository extends JpaRepository<Journal, UUID>{
	
}
