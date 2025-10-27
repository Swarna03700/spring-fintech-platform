package com.fintech.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
	
	Optional<Wallet> findByUser(UserEntity user);
}
