package com.fintech.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.entity.Entry;
import com.fintech.wallet.entity.Journal;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.repository.EntryRepository;
import com.fintech.wallet.utilities.Utils;

@ExtendWith(MockitoExtension.class)
public class EntryServiceTest {
	
	@InjectMocks
	private EntryService entryService;
	
	@Mock
	private EntryRepository entryRepository;
	
	@Test
	void testGetLedgerEntry_returnEntryDto() {
		String entryId = "cc0a579a-1f6b-4a52-94c1-541689e90c28";
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setId(UUID.randomUUID());
		
		Journal journal = new Journal();
		journal.setId(UUID.randomUUID());
		
		Entry entry = new Entry();
		entry.setId(UUID.fromString(entryId));
		entry.setAmount(new BigInteger("100"));
		entry.setStatus("POSTED");
		entry.setDirection("DEBIT");
		entry.setLedgerAccountId(ledgerAccount);
		entry.setJournal(journal);
		entry.setCreatedAt(Instant.now());
		entry.setUpdatedAt(Instant.now());
		
		when(entryRepository.findById(UUID.fromString(entryId))).thenReturn(Optional.of(entry));
		
		EntryDto entryDto = entryService.getEntry(entryId);
		
		assertThat(entryDto).isNotNull();
		
	}
	
	@Test
	void testGetAllEntries_returnListOfEntryDtos() {
		String entryId = "cc0a579a-1f6b-4a52-94c1-541689e90c28";
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setId(UUID.randomUUID());
		
		Journal journal = new Journal();
		journal.setId(UUID.randomUUID());
		
		List<Entry> entries = new ArrayList<>();
		
		Entry entry = new Entry();
		entry.setId(UUID.fromString(entryId));
		entry.setAmount(new BigInteger("100"));
		entry.setStatus("POSTED");
		entry.setDirection("DEBIT");
		entry.setLedgerAccountId(ledgerAccount);
		entry.setJournal(journal);
		entry.setCreatedAt(Instant.now());
		entry.setUpdatedAt(Instant.now());
		
		entries.add(entry);
		
		when(entryRepository.findAll()).thenReturn(entries);
		
		List<EntryDto> entryDtos = entryService.getAllEntries();
		
		assertThat(entryDtos).isNotNull();
		assertThat(entryDtos).hasSize(1);
		
		assertThat(entryDtos.get(0).id()).isEqualTo(entry.getId().toString());
		assertThat(entryDtos.get(0).amount()).isEqualTo(Utils.convertToFloatingPoint(entry.getAmount()));
		assertThat(entryDtos.get(0).status()).isEqualTo(entry.getStatus());
		assertThat(entryDtos.get(0).direction()).isEqualTo(entry.getDirection());
		assertThat(entryDtos.get(0).ledgerAccountId()).isEqualTo(entry.getLedgerAccountId().getId().toString());
		assertThat(entryDtos.get(0).journalId()).isEqualTo(entry.getJournal().getId().toString());
		assertThat(entryDtos.get(0).createdAt()).isEqualTo(entry.getCreatedAt().toString());
		assertThat(entryDtos.get(0).updatedAt()).isEqualTo(entry.getUpdatedAt().toString());
	}
	
}
