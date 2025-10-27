package com.fintech.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.WalletResponse;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.service.UserService;
import com.fintech.wallet.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
	
	private final UserService userService;
	private final WalletService walletService;
	
	public WalletController(WalletService walletService, UserService userService) {
		this.walletService = walletService;
		this.userService = userService;
	}
	
	/**
	 * Get the wallet of the currently authenticated user.
	 */
	@GetMapping
	public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal Jwt jwt) {
		UserEntity currentUser = userService.getOrCreateUserEntity(jwt);
		
		WalletResponse wallet = walletService.getWalletForUser(currentUser);
		
		return ResponseEntity.ok(wallet);
	}
}
