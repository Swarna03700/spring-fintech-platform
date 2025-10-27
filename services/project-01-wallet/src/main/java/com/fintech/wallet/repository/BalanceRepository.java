package com.fintech.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.LedgerAccountBalances;

public interface BalanceRepository extends JpaRepository<LedgerAccountBalances, UUID> {

}
