package com.fintech.wallet.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fintech.wallet.entity.Entry;
import com.fintech.wallet.entity.Journal;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.repository.EntryRepository;
import com.fintech.wallet.repository.JournalRepository;
import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;
import com.fintech.wallet.utilities.Utils;

import static org.hamcrest.Matchers.closeTo;

@AutoConfigureMockMvc
public class EntryIntegrationTest extends AbstractPostgresIntegrationTest {
	
	@Autowired
	private LedgerRepository ledgerRepository;
	
	@Autowired
	private LedgerAccountRepository ledgerAccountRepository;
	
	@Autowired
	private JournalRepository journalRepository;
	
	@Autowired
	private EntryRepository entryRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	Entry setupEntry() {
		Ledger ledger = new Ledger();
		ledger.setName("Wallet Ledger");
		ledger.setDescription("");
		
		Ledger savedLedger = ledgerRepository.save(ledger);
		
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setName("Wallet Account");
		ledgerAccount.setDescription("");
		ledgerAccount.setNormalBalance("credit");
		ledgerAccount.setBalance(new BigInteger("0"));
		ledgerAccount.setLedger(savedLedger);
		
		LedgerAccount savedLedgerAccount = ledgerAccountRepository.save(ledgerAccount);
		
		Journal journal = new Journal();
		journal.setDescription("");
		journal.setStatus("POSTED");
		journal.setLedger(savedLedger);
		
		Journal savedJournal = journalRepository.save(journal);
		
		
		Entry entry = new Entry();
		entry.setAmount(new BigInteger("100"));
		entry.setStatus("POSTED");
		entry.setDirection("DEBIT");
		entry.setLedgerAccountId(savedLedgerAccount);
		entry.setJournal(savedJournal);
		
		return entry;
	}
	
	@Test
	void testGetEntry_returnOkAndEntryDto() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String scope = "admin";
		
		Entry savedEntry = entryRepository.save(setupEntry());
		
		mockMvc.perform(get("/api/ledger_entries/{id}", savedEntry.getId().toString())
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", scope))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(savedEntry.getId().toString()))
		.andExpect(jsonPath("$.amount", closeTo(Utils.convertToFloatingPoint(savedEntry.getAmount()).doubleValue(), 0.001)))
		.andExpect(jsonPath("$.status").value(savedEntry.getStatus()))
		.andExpect(jsonPath("$.direction").value(savedEntry.getDirection()))
		.andExpect(jsonPath("$.ledger_account_id").value(savedEntry.getLedgerAccountId().getId().toString()))
		.andExpect(jsonPath("$.journal_id").value(savedEntry.getJournal().getId().toString()))
		.andExpect(jsonPath("$.created_at").value(savedEntry.getCreatedAt().toString()))
		.andExpect(jsonPath("$.updated_at").value(savedEntry.getUpdatedAt().toString()));
		
	}
	
	@Test
	void testGetAllEntries_returnOkAndListOfEntryDtos() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String scope = "admin";
		
		Entry savedEntry = entryRepository.save(setupEntry());
		
		List<Entry> entries = new ArrayList<>();
		entries.add(savedEntry);
		
		mockMvc.perform(get("/api/ledger_entries")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", scope))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].id").value(savedEntry.getId().toString()))
		.andExpect(jsonPath("$.[0].amount", closeTo(Utils.convertToFloatingPoint(savedEntry.getAmount()).doubleValue(), 0.001)))
		.andExpect(jsonPath("$.[0].status").value(savedEntry.getStatus()))
		.andExpect(jsonPath("$.[0].direction").value(savedEntry.getDirection()))
		.andExpect(jsonPath("$.[0].ledger_account_id").value(savedEntry.getLedgerAccountId().getId().toString()))
		.andExpect(jsonPath("$.[0].journal_id").value(savedEntry.getJournal().getId().toString()))
		.andExpect(jsonPath("$.[0].created_at").isNotEmpty())
		.andExpect(jsonPath("$.[0].updated_at").isNotEmpty());
	}
	
	@AfterEach
	void cleanup() {
		entryRepository.deleteAll();
		journalRepository.deleteAll();
		ledgerAccountRepository.deleteAll();
		ledgerRepository.deleteAll();
	}
	
}
