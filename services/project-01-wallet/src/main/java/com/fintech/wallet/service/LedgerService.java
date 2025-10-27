package com.fintech.wallet.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fintech.wallet.dto.CreateLedgerRequest;
import com.fintech.wallet.dto.LedgerDto;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.repository.LedgerRepository;

@Service
public class LedgerService {
	
	private LedgerRepository ledgerRepository;
	
	public LedgerService(LedgerRepository ledgerRepository) {
		this.ledgerRepository = ledgerRepository;
	}
	
	public LedgerDto createLedger(CreateLedgerRequest request) {
		Ledger ledger = new Ledger();
		ledger.setName(request.name());
		ledger.setDescription(request.description());
		ledger.setMetadata(request.metadata());
		
		Ledger savedLedger = ledgerRepository.save(ledger);
		return new LedgerDto(
				savedLedger.getId().toString(), 
				savedLedger.getName(), 
				savedLedger.getDescription(), 
				savedLedger.getMetadata(), 
				savedLedger.getCreatedAt().toString(), 
				savedLedger.getUpdatedAt().toString());
	}
	
	public LedgerDto getLedgerById(String id) {
		UUID ledgerId = UUID.fromString(id);
		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new LedgerNotFoundException("Ledger with id " + ledgerId + " not found"));
		System.out.println(ledger.getName());
		
		return new LedgerDto(
				ledger.getId().toString(),
				ledger.getName(), 
				ledger.getDescription(), 
				ledger.getMetadata(), 
				ledger.getCreatedAt().toString(), 
				ledger.getUpdatedAt().toString()
				);
	}
	
	public LedgerDto updateLedgerById(String id, CreateLedgerRequest request) {
		UUID ledgerId = UUID.fromString(id);
		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new LedgerNotFoundException("Ledger with id " + id + " not found"));
		ledger.setName(request.name());
		ledger.setDescription(request.description());
		ledger.setMetadata(request.metadata());
		
		Ledger savedLedger = ledgerRepository.save(ledger);
		return new LedgerDto(
				savedLedger.getId().toString(), 
				savedLedger.getName(), 
				savedLedger.getDescription(), 
				savedLedger.getMetadata(), 
				savedLedger.getCreatedAt().toString(),
				savedLedger.getUpdatedAt().toString());
	}
	
	public boolean deleteLedgerById(String id) {
		UUID ledgerId = UUID.fromString(id);
		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new LedgerNotFoundException("Ledger with id "+ id + " not found"));
		ledgerRepository.delete(ledger);
		return true;
	}
}
