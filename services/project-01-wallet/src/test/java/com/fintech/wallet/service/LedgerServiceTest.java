package com.fintech.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateLedgerRequest;
import com.fintech.wallet.dto.LedgerDto;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.repository.LedgerRepository;

@ExtendWith(MockitoExtension.class)
public class LedgerServiceTest {
	
	@Mock
	private LedgerRepository ledgerRepository;
	
	@InjectMocks
	private LedgerService ledgerService;
	
	Map<String, Object> metadata = new HashMap<>();
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test
	void testCreateLedger() {
		Instant createdAt = Instant.now();
		Instant updatedAt = Instant.now();
		
		CreateLedgerRequest request = new CreateLedgerRequest("Business ledger", "Tracks all money movements", metadata);
		
		Ledger ledger = new Ledger();
		ledger.setId(UUID.randomUUID());
		ledger.setName("Business Ledger");
		ledger.setDescription("Tracks all money movements");
		ledger.setMetadata(metadata);
		ledger.setCreatedAt(createdAt);
		ledger.setUpdatedAt(updatedAt);
		when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
		
		LedgerDto resultDto = ledgerService.createLedger(request);
		
		assertThat(resultDto).isNotNull();
		assertThat(resultDto.id()).isNotNull();
		assertThat(resultDto.name()).isEqualTo(ledger.getName());
		assertThat(resultDto.description()).isEqualTo(ledger.getDescription());
		assertThat(resultDto.createdAt()).isNotNull();
		assertThat(resultDto.updatedAt()).isNotNull();
		
		verify(ledgerRepository, times(1)).save(any(Ledger.class));
	}
	
	@Test
	void testGetLedgerById_whenLedgerExists() {
		String id = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
		UUID ledgerId = UUID.fromString(id);
		Instant createdAt = Instant.now();
		Instant updatedAt = Instant.now();
		
		Ledger ledger = new Ledger();
		ledger.setId(UUID.randomUUID());
		ledger.setName("Wallet Ledger");
		ledger.setDescription("Tracks money movements");
		ledger.setMetadata(metadata);
		ledger.setCreatedAt(createdAt);
		ledger.setUpdatedAt(updatedAt);
		
		when(ledgerRepository.findById(ledgerId)).thenReturn(Optional.of(ledger));
		
		LedgerDto dto = ledgerService.getLedgerById(id);
		
		assertThat(dto).isNotNull();
		assertThat(dto.id()).isNotNull();
		assertThat(dto.name()).isEqualTo(ledger.getName());
		assertThat(dto.description()).isEqualTo(ledger.getDescription());
		assertThat(dto.createdAt()).isNotNull();
		assertThat(dto.updatedAt()).isNotNull();
	}
	
	@Test
	void testGetLedgerById_whenLedgerDoesNotExist_thenThrowException() {
		String id = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
		UUID ledgerId = UUID.fromString(id);
		
		when(ledgerRepository.findById(ledgerId)).thenReturn(Optional.empty());
		
		LedgerNotFoundException thrownException = assertThrows(LedgerNotFoundException.class, 
				() -> ledgerService.getLedgerById(id)
				);
	}
	
	@Test
	void testUpdateLedger_ifNotExists_thenReturnNotFound() {
		String id = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
		UUID ledgerId = UUID.fromString(id);
		
		when(ledgerRepository.findById(ledgerId)).thenReturn(Optional.empty());
		
		LedgerNotFoundException thrownException = assertThrows(LedgerNotFoundException.class, 
				() -> ledgerService.getLedgerById(id)
				);
	}
	
	@Test
	void testUpdateLedger_ifSuccessful_returnOk() throws Exception {
		Ledger ledger = new Ledger();
		ledger.setName("Business Ledger");
		ledger.setDescription("Tracks money movements");
		ledger.setMetadata(metadata);
		String id = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
		UUID ledgerId = UUID.fromString(id);
		when(ledgerRepository.findById(ledgerId)).thenReturn(Optional.of(ledger));
		
		CreateLedgerRequest request = new CreateLedgerRequest("Wallet Ledger", "", metadata);
		
		Ledger updatedLedger = new Ledger();
		updatedLedger.setId(ledgerId);
		updatedLedger.setName(request.name());
		updatedLedger.setDescription(request.description());
		updatedLedger.setMetadata(metadata);
		updatedLedger.setCreatedAt(Instant.now());
		updatedLedger.setUpdatedAt(Instant.now());
		when(ledgerRepository.save(any(Ledger.class))).thenReturn(updatedLedger);
		LedgerDto updatedLedgerDto = ledgerService.updateLedgerById(id, request);
		
		assertThat(updatedLedgerDto).isNotNull();
		assertThat(updatedLedgerDto.name()).isEqualTo("Wallet Ledger");
		assertThat(updatedLedgerDto.description()).isEqualTo("");
	}
}
