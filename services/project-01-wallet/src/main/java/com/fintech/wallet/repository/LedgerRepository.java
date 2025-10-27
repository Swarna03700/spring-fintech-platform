package com.fintech.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, UUID>{
	
	
}
