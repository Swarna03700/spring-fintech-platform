package com.fintech.wallet.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fintech.wallet.dto.CreateJournalDto;
import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.dto.EntryRequestDto;
import com.fintech.wallet.dto.JournalDto;
import com.fintech.wallet.entity.Entry;
import com.fintech.wallet.entity.Journal;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.exception.JournalNotFoundException;
import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.repository.EntryRepository;
import com.fintech.wallet.repository.JournalRepository;
import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@Service
public class JournalService {
	
	private LedgerRepository ledgerRepository;
	private LedgerAccountRepository ledgerAccountRepository;
	private JournalRepository journalRepository;
	private EntryRepository entryRepository;
	
	public JournalService(LedgerRepository ledgerRepository, LedgerAccountRepository ledgerAccountRepository, JournalRepository journalRepository, EntryRepository entryRepository) {
		this.ledgerRepository = ledgerRepository;
		this.journalRepository = journalRepository;
		this.ledgerAccountRepository = ledgerAccountRepository;
		this.entryRepository = entryRepository;
	}
	
	public JournalDto createJournal(CreateJournalDto request) {
		Ledger legder = ledgerRepository.findById(UUID.fromString(request.ledgerId()))
				.orElseThrow(
						() -> new LedgerNotFoundException("Ledger with id "+ request.ledgerId() + " not found")
						);
		
		Journal journal = new Journal();
		journal.setDescription(request.description());
		journal.setStatus("POSTED");
		journal.setLedger(legder);
		
		Journal savedJournal = journalRepository.save(journal);
		List<Entry> entries = new ArrayList<>();
		for (EntryRequestDto entryDto : request.entries()) {
			LedgerAccount ledgerAccount = ledgerAccountRepository.findById(UUID.fromString(entryDto.ledgerAcoountId()))
					.orElseThrow(
							() -> new LedgerAccountNotFoundException("Ledger account with id "+ entryDto.ledgerAcoountId() +" not found")
							);
			BigDecimal conversionFactor = new BigDecimal("100");
			BigDecimal amountInPaisa = entryDto.amount().multiply(conversionFactor);
			BigInteger paisa = amountInPaisa.toBigInteger();
			Entry entry = new Entry();
			entry.setDirection(entryDto.direction());
			entry.setAmount(paisa);
			entry.setStatus("POSTED");
			entry.setJournal(savedJournal);
			entry.setLedgerAccountId(ledgerAccount);
			entries.add(entry);
		}
		List<EntryDto> allEntries = new ArrayList<>();
		List<Entry> savedEntries = entryRepository.saveAll(entries);
		for(Entry entry : savedEntries) {
			EntryDto result = new EntryDto(
					entry.getId().toString(),
					convertToFloatingPoint(entry.getAmount()),
					entry.getStatus(),
					entry.getDirection(),
					entry.getLedgerAccountId().getId().toString(),
					entry.getJournal().getId().toString(),
					entry.getCreatedAt().toString(),
					entry.getUpdatedAt().toString()
					);
			allEntries.add(result);
		}
		
		
		return new JournalDto(
					savedJournal.getId().toString(), 
					savedJournal.getDescription(), 
					savedJournal.getStatus(), 
					savedJournal.getLedger().getId().toString(), 
					allEntries, 
					savedJournal.getCreatedAt().toString(), 
					savedJournal.getUpdatedAt().toString());
	}
	
	BigDecimal convertToFloatingPoint(BigInteger amount) {
		int scale = 2;
		BigDecimal floatingPoint = new BigDecimal(amount, scale);
		return floatingPoint;
	}
	
	public JournalDto getJournalById(String id) {
		UUID journalId = UUID.fromString(id);
		Journal journal = journalRepository.findById(journalId)
				.orElseThrow(
						() -> new JournalNotFoundException("Journal with id " + id + " not found")
						);
		// Retrieve the entries from Entry table associated with that transaction
		List<Entry> entries = entryRepository.findByJournalId(journalId);
		List<EntryDto> entryDtos = new ArrayList<>();
		for(Entry entry : entries) {
			EntryDto entryDto = new EntryDto(
									entry.getId().toString(),
									convertToFloatingPoint(entry.getAmount()),
									entry.getStatus(),
									entry.getDirection(),
									entry.getLedgerAccountId().getId().toString(),
									entry.getJournal().getId().toString(),
									entry.getCreatedAt().toString(),
									entry.getUpdatedAt().toString()
					);
			entryDtos.add(entryDto);
		}
		return new JournalDto(
					journal.getId().toString(), 
					journal.getDescription(), 
					journal.getStatus(), 
					journal.getLedger().getId().toString(), 
					entryDtos, 
					journal.getCreatedAt().toString(), 
					journal.getUpdatedAt().toString());
	}
}
