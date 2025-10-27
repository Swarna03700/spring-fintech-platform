package com.fintech.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String>{
	
	Optional<Transaction> findByTransactionId(String transactionId);
}
