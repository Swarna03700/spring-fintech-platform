package com.fintech.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.CreateLedgerAccount;
import com.fintech.wallet.dto.LedgerAccountDto;
import com.fintech.wallet.service.LedgerAccountService;

@RestController
@RequestMapping("/api")
public class LedgerAccountController {
	
	private LedgerAccountService ledgerAccountService;
	
	public LedgerAccountController(LedgerAccountService ledgerAccountService) {
		this.ledgerAccountService = ledgerAccountService;
	}
	
	@PostMapping("/ledger_accounts")
	public ResponseEntity<LedgerAccountDto> createLedgerAccount(@RequestBody CreateLedgerAccount request) {
		LedgerAccountDto response = ledgerAccountService.createLedgerAccount(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/ledger_accounts/{id}")
	public ResponseEntity<LedgerAccountDto> getLedgerAccount(@PathVariable("id") String id) {
		LedgerAccountDto response = ledgerAccountService.getLedgerAccountById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
