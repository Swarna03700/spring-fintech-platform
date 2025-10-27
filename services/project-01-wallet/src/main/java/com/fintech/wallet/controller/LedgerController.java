package com.fintech.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.CreateLedgerRequest;
import com.fintech.wallet.dto.LedgerDto;
import com.fintech.wallet.service.LedgerService;


@RestController
@RequestMapping("/api")
public class LedgerController {
	
	private LedgerService ledgerService;
	
	public LedgerController(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}
	
	@PostMapping("/ledgers")
	public ResponseEntity<LedgerDto> createLedger(@RequestBody CreateLedgerRequest request) {
		LedgerDto response = ledgerService.createLedger(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/ledgers/{id}")
	public ResponseEntity<LedgerDto> getLedgerById(@PathVariable("id") String id) {
		
		LedgerDto response = ledgerService.getLedgerById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PatchMapping("/ledgers/{id}")
	public ResponseEntity<LedgerDto> updateLedgerById(@PathVariable("id") String id, @RequestBody CreateLedgerRequest request) {
		
		LedgerDto response = ledgerService.updateLedgerById(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping("/ledgers/{id}")
	public ResponseEntity<Void> deleteLedgerById(@PathVariable("id") String id) {
		ledgerService.deleteLedgerById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
