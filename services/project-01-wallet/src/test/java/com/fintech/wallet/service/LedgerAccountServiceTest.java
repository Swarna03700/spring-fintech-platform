package com.fintech.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fintech.wallet.dto.CreateLedgerAccount;
import com.fintech.wallet.dto.LedgerAccountDto;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@ExtendWith(MockitoExtension.class)
public class LedgerAccountServiceTest {
	
	@Mock
	private LedgerAccountRepository ledgerAccountRepository;
	
	@Mock
	private LedgerRepository ledgerRepository;
	
	@InjectMocks
	private LedgerAccountService ledgerAccountService;
	
	Map<String, Object> metadata = new HashMap<>();
	
	String name = "Wallet Account";
	String description = "";
	String normalBalance = "credit";
	String ledgerId = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
	String accountId = "a3b9e1f1-th9c-4d1b-8e9f-6a7b8c9d0e1f";
	
	
	@Test
	void testCreateLedgerAccount_returnCreated() {
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		
		Ledger ledger = new Ledger();
		ledger.setId(UUID.fromString(ledgerId));
		ledger.setName("Wallet Ledger");
		ledger.setDescription("");
		ledger.setMetadata(metadata);
		ledger.setCreatedAt(Instant.now());
		ledger.setUpdatedAt(Instant.now());
		
		when(ledgerRepository.findById(UUID.fromString(ledgerId))).thenReturn(Optional.of(ledger));
		
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setId(UUID.randomUUID());
		ledgerAccount.setName(request.name());
		ledgerAccount.setDescription(request.description());
		ledgerAccount.setMetadata(metadata);
		ledgerAccount.setNormalBalance(request.normalBalance());
		ledgerAccount.setBalance(new BigInteger("100"));
		ledgerAccount.setLedger(ledger);
		ledgerAccount.setCreatedAt(Instant.now());
		ledgerAccount.setUpdatedAt(Instant.now());
		

		when(ledgerAccountRepository.save(any(LedgerAccount.class))).thenReturn(ledgerAccount);
		
		LedgerAccountDto ledgerAccountDto = ledgerAccountService.createLedgerAccount(request);
		
		assertThat(ledgerAccountDto).isNotNull();
		assertThat(ledgerAccountDto.name()).isEqualTo(ledgerAccount.getName());
		assertThat(ledgerAccountDto.description()).isEqualTo(ledgerAccount.getDescription());
		assertThat(ledgerAccountDto.metadata()).isEqualTo(ledgerAccount.getMetadata());
		assertThat(ledgerAccountDto.normalBalance()).isEqualTo(ledgerAccount.getNormalBalance());
		assertThat(ledgerAccountDto.balance()).isEqualTo(new BigDecimal("1.00"));
		
		verify(ledgerAccountRepository, times(1)).save(any(LedgerAccount.class));
	}
	
	@Test
	void testCreateLedgerAccount_ifLedgerNotFound_throwException() {
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		
		Ledger ledger = new Ledger();
		ledger.setId(UUID.fromString(ledgerId));
		ledger.setName("Wallet Ledger");
		ledger.setDescription("");
		ledger.setMetadata(metadata);
		ledger.setCreatedAt(Instant.now());
		ledger.setUpdatedAt(Instant.now());
		
		when(ledgerRepository.findById(UUID.fromString(ledgerId))).thenThrow(new LedgerNotFoundException("Ledger with id "+ ledgerId + " not found"));
		
		LedgerNotFoundException thrownException = assertThrows(LedgerNotFoundException.class, 
						() -> ledgerAccountService.createLedgerAccount(request));
		System.out.println(thrownException);
	}
	
	@Test
	void testGetLedgerAccountById_returnLedgerAccountDto() {
		String id = "98765432-10ab-cdef-0123-456789abcdef";
		LedgerAccount ledgerAccount = ledgerAccountSetUp();
		when(ledgerAccountRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(ledgerAccount));
		
		LedgerAccountDto ledgerAccountDto = ledgerAccountService.getLedgerAccountById(id);
		
		assertThat(ledgerAccountDto).isNotNull();
		assertThat(ledgerAccountDto.name()).isEqualTo(ledgerAccount.getName());
		assertThat(ledgerAccountDto.description()).isEqualTo(ledgerAccount.getDescription());
		assertThat(ledgerAccountDto.metadata()).isEqualTo(ledgerAccount.getMetadata());
		assertThat(ledgerAccountDto.normalBalance()).isEqualTo(ledgerAccount.getNormalBalance());
		assertThat(ledgerAccountDto.balance()).isEqualTo(new BigDecimal("1.00"));
		assertThat(ledgerAccountDto.ledgerId()).isEqualTo(ledgerAccount.getLedger().getId().toString());
		assertThat(ledgerAccountDto.createdAt()).isNotNull();
		assertThat(ledgerAccountDto.updatedAt()).isNotNull();
		
	}
	
	@Test
	void testGetLedgerAccountById_ifNotExists_throwException() {
		String id = "98765432-10ab-cdef-0123-456789abcdef";
		
		when(ledgerAccountRepository.findById(UUID.fromString(id))).thenThrow(new LedgerAccountNotFoundException("Ledger account with id "+ id + " not found"));
		
		LedgerAccountNotFoundException thrownException = assertThrows(LedgerAccountNotFoundException.class, 
				() -> ledgerAccountService.getLedgerAccountById(id)
				);
		System.out.println(thrownException);
	}
	
	LedgerAccount ledgerAccountSetUp() {
		Ledger ledger = new Ledger();
		ledger.setId(UUID.fromString("b8d3f8f0-3b7c-4a1e-8f9b-6d6a1f8c7e4d"));
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"));
		ledgerAccount.setName("Wallet Account");
		ledgerAccount.setDescription("");
		ledgerAccount.setMetadata(metadata);
		ledgerAccount.setNormalBalance("credit");
		ledgerAccount.setBalance(new BigInteger("100"));
		ledgerAccount.setLedger(ledger);
		ledgerAccount.setCreatedAt(Instant.now());
		ledgerAccount.setUpdatedAt(Instant.now());
		
		return ledgerAccount;
	}
	
}
