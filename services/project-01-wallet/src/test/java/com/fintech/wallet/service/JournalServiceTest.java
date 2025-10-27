package com.fintech.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.fintech.wallet.dto.CreateJournalDto;
import com.fintech.wallet.dto.EntryRequestDto;
import com.fintech.wallet.dto.JournalDto;
import com.fintech.wallet.entity.Entry;
import com.fintech.wallet.entity.Journal;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.repository.EntryRepository;
import com.fintech.wallet.repository.JournalRepository;
import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@ExtendWith(MockitoExtension.class)
public class JournalServiceTest {
	
	@Mock
	private JournalRepository journalRepository;
	@Mock
	private LedgerRepository ledgerRepository;
	@Mock
	private LedgerAccountRepository ledgerAccountRepository;
	
	@Mock
	private EntryRepository entryRepository;
	
	@InjectMocks
	private JournalService journalService;
	
	String ledgerId = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
	String ledgerAccountId1 = "b8d3f8f0-3b7c-4a1e-8f9b-6d6a1f8c7e4d";
	String ledgerAccountId2 = "98765432-10ab-cdef-0123-456789abcdef";
	String journalId = "123e4567-e89b-12d3-a456-426614174000";
	String description = "Wallet to wallet transfer";
	
	@Test
	void testCreateJournal_returnJournalDto() {
		Ledger ledger = new Ledger();
		ledger.setId(UUID.fromString(ledgerId));
		when(ledgerRepository.findById(UUID.fromString(ledgerId))).thenReturn(Optional.of(ledger));
		
		LedgerAccount ledgerAccount1 = new LedgerAccount();
		ledgerAccount1.setId(UUID.fromString(ledgerAccountId1));
		LedgerAccount ledgerAccount2 = new LedgerAccount();
		ledgerAccount2.setId(UUID.fromString(ledgerAccountId2));
		when(ledgerAccountRepository.findById(UUID.fromString(ledgerAccountId1))).thenReturn(Optional.of(ledgerAccount1));
		when(ledgerAccountRepository.findById(UUID.fromString(ledgerAccountId2))).thenReturn(Optional.of(ledgerAccount2));
		
		Journal journal = new Journal();
		journal.setId(UUID.fromString(journalId));
		journal.setDescription(description);
		journal.setLedger(ledger);
		journal.setCreatedAt(Instant.now());
		journal.setUpdatedAt(Instant.now());
		
		when(journalRepository.save(any(Journal.class))).thenReturn(journal);
		
		EntryRequestDto debitEntry = new EntryRequestDto("DEBIT", new BigDecimal("100"), ledgerAccountId1);
		EntryRequestDto creditEntry = new EntryRequestDto("CREDIT", new BigDecimal("100"), ledgerAccountId2);
		List<EntryRequestDto> requestEntries = new ArrayList<>();
		requestEntries.add(debitEntry);
		requestEntries.add(creditEntry);
		CreateJournalDto request = new CreateJournalDto(description, ledgerId, requestEntries);
		
		Entry entry1 = new Entry();
		entry1.setId(UUID.randomUUID());
		entry1.setDirection("DEBIT");
		entry1.setAmount(new BigInteger("10000"));
		entry1.setLedgerAccountId(ledgerAccount1);
		entry1.setJournal(journal);
		entry1.setCreatedAt(Instant.now());
		entry1.setUpdatedAt(Instant.now());
		
		Entry entry2 = new Entry();
		entry2.setId(UUID.randomUUID());
		entry2.setDirection("CREDIT");
		entry2.setAmount(new BigInteger("10000"));
		entry2.setLedgerAccountId(ledgerAccount2);
		entry2.setJournal(journal);
		entry2.setCreatedAt(Instant.now());
		entry2.setUpdatedAt(Instant.now());
		
		List<Entry> allEntries = new ArrayList<>();
		allEntries.add(entry1);
		allEntries.add(entry2);
		
		when(entryRepository.saveAll(anyList())).thenReturn(allEntries);
		JournalDto journalDto = journalService.createJournal(request);
		
		assertThat(journalDto).isNotNull();
		assertThat(journalDto.description()).isEqualTo(journal.getDescription());
	}
	
	@Test
	void testGetJournalById_returnJournalDto() {
		
		Ledger ledger = new Ledger();
		ledger.setId(UUID.randomUUID());
		
		Journal journal = new Journal();
		journal.setId(UUID.randomUUID());
		journal.setDescription("");
		journal.setStatus("POSTED");
		journal.setLedger(ledger);
		journal.setCreatedAt(Instant.now());
		journal.setUpdatedAt(Instant.now());
		
		when(journalRepository.findById(UUID.fromString(journalId))).thenReturn(Optional.of(journal));
		
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setId(UUID.randomUUID());
		
		Entry entry = new Entry();
		entry.setId(UUID.randomUUID());
		entry.setAmount(new BigInteger("100"));
		entry.setStatus("POSTED");
		entry.setLedgerAccountId(ledgerAccount);
		entry.setJournal(journal);
		entry.setCreatedAt(Instant.now());
		entry.setUpdatedAt(Instant.now());
		
		when(entryRepository.findByJournalId(UUID.fromString(journalId))).thenReturn(List.of(entry));
		
		JournalDto journalDto = journalService.getJournalById(journalId);
		
		assertThat(journalDto).isNotNull();
	}
}
