package com.fintech.wallet.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.fintech.wallet.dto.CreateLedgerAccount;
import com.fintech.wallet.dto.LedgerAccountDto;

import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;

import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;

import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@Service
public class LedgerAccountService {
	
	private LedgerAccountRepository ledgerAccountRepository;
	private LedgerRepository ledgerRepository;
	
	public LedgerAccountService(LedgerAccountRepository ledgerAccountRepository, LedgerRepository ledgerRepository) {
		this.ledgerAccountRepository = ledgerAccountRepository;
		this.ledgerRepository = ledgerRepository;
	}
	
	Map<String, Object> metadata = new HashMap<>();
	
	public LedgerAccountDto createLedgerAccount(CreateLedgerAccount request) {
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setName(request.name());
		ledgerAccount.setDescription(request.description());
		ledgerAccount.setMetadata(request.metadata());
		ledgerAccount.setNormalBalance(request.normalBalance());
		ledgerAccount.setBalance(BigInteger.ZERO);
		
		Ledger ledger = ledgerRepository.findById(UUID.fromString(request.ledgerId()))
				.orElseThrow(() -> new LedgerNotFoundException("Ledger with id "+ request.ledgerId() + " not found"));
		
		ledgerAccount.setLedger(ledger);
		
		LedgerAccount saved = ledgerAccountRepository.save(ledgerAccount);
		int scale  = 2;
		BigDecimal balance = new BigDecimal(saved.getBalance(), scale);
		return new LedgerAccountDto(
								saved.getId().toString(), 
								saved.getName(), 
								saved.getDescription(),
								saved.getMetadata(), 
								saved.getNormalBalance(), 
								balance, 
								saved.getLedger().getId().toString(), 
								saved.getCreatedAt().toString(), 
								saved.getUpdatedAt().toString());
	}
	
	public LedgerAccountDto getLedgerAccountById(String id) {
		UUID ledgerAccountId = UUID.fromString(id);
		
		LedgerAccount ledgerAccount = ledgerAccountRepository.findById(ledgerAccountId)
				.orElseThrow(() -> new LedgerAccountNotFoundException("Ledger account with id " + id + " not found"));
		return new LedgerAccountDto(
				ledgerAccount.getId().toString(), 
				ledgerAccount.getName(), 
				ledgerAccount.getDescription(), 
				ledgerAccount.getMetadata(),
				ledgerAccount.getNormalBalance(), 
				new BigDecimal(ledgerAccount.getBalance(), 2), 
				ledgerAccount.getLedger().getId().toString(), 
				ledgerAccount.getCreatedAt().toString(), 
				ledgerAccount.getUpdatedAt().toString());
	}
}
