package com.fintech.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.TransactionResponse;
import com.fintech.wallet.dto.TransferRequest;
import com.fintech.wallet.service.TransactionService;

@RestController
@RequestMapping
public class TransactionController {

	private TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
/*
	@PostMapping("/transfers")
	public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		
		String senderAuthId = jwt.getSubject();
		TransactionResponse response = transactionService.performTransfer(senderAuthId, request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/admin/transactions/{transactionId}")
	public ResponseEntity<TransactionResponse> getATransaction(@PathVariable("transactionId") String transactionId, @AuthenticationPrincipal Jwt jwt) {
		TransactionResponse response = transactionService.getTransactionById(transactionId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
}
