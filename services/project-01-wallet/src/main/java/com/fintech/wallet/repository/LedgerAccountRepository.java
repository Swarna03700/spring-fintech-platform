package com.fintech.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.LedgerAccount;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, UUID> {
	
}
