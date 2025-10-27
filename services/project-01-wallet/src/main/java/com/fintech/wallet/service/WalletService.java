package com.fintech.wallet.service;

import org.springframework.stereotype.Service;

import com.fintech.wallet.dto.WalletResponse;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.exception.WalletNotFoundException;
import com.fintech.wallet.repository.WalletRepository;

@Service
public class WalletService {
	
	private final WalletRepository walletRepository;
	
	public WalletService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}
	
	public WalletResponse getWalletForUser(UserEntity user) {
		Wallet wallet = walletRepository.findByUser(user)
				.orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
		return new WalletResponse(wallet.getWalletId().toString(), wallet.getBalance(), wallet.getCurrency(), wallet.getIsActive());
	}
	
//	public WalletResponse retrieveWallet(String userId) {
//		WalletEntity wallet = walletRepository.findByUserId(userId)
//				.orElseThrow(()-> new WalletNotFoundException("Wallet not found"));
//		return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getCurrency());
//	}
	
}
