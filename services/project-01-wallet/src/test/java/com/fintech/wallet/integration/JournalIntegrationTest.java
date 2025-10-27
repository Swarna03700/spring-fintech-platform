package com.fintech.wallet.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateJournalDto;
import com.fintech.wallet.dto.EntryRequestDto;

import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;

import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@AutoConfigureMockMvc
public class JournalIntegrationTest extends AbstractPostgresIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private LedgerRepository ledgerRepository;
	
	@Autowired
	private LedgerAccountRepository ledgerAccountRepository;
	
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test
	void testCreateJournal_returnCreatedAndJournalDto() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String scope = "admin";
		String ledgerId = setUpLedger().getId().toString();
		String ledgerAccountId = setUpLedgerAccount().getId().toString();
		String receiverLedgerAccountId = setUpReceiverLedgerAccount().getId().toString();
		
		EntryRequestDto debitRequest = new EntryRequestDto(
											"DEBIT", 
											new BigDecimal("100"),
											ledgerAccountId
											);
		
		EntryRequestDto creditRequest = new EntryRequestDto(
				"CREDIT", 
				new BigDecimal("100"),
				receiverLedgerAccountId
				);
		
		List<EntryRequestDto> entries = new ArrayList<>();
		entries.add(debitRequest);
		entries.add(creditRequest);
		CreateJournalDto request = new CreateJournalDto(
										"Transfer to another wallet", 
										ledgerId, 
										entries);
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", scope)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.description").value("Transfer to another wallet"))
		.andExpect(jsonPath("$.status").value("POSTED"))
		.andExpect(jsonPath("$.ledger_id").value(ledgerId))
		.andExpect(jsonPath("$.entries", hasSize(2)))
		.andExpect(jsonPath("$.entries[0].id").isNotEmpty())
		.andExpect(jsonPath("$.entries[0].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[0].status", is("POSTED")))
		.andExpect(jsonPath("$.entries[0].direction", is("DEBIT")))
		.andExpect(jsonPath("$.entries[0].ledger_account_id").value(ledgerAccountId))
		.andExpect(jsonPath("$.entries[0].journal_id").isNotEmpty())
		.andExpect(jsonPath("$.entries[0].created_at").isNotEmpty())
		.andExpect(jsonPath("$.entries[0].updated_at").isNotEmpty())
		.andExpect(jsonPath("$.entries[1].id").isNotEmpty())
		.andExpect(jsonPath("$.entries[1].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[1].status", is("POSTED")))
		.andExpect(jsonPath("$.entries[1].direction", is("CREDIT")))
		.andExpect(jsonPath("$.entries[1].ledger_account_id").value(receiverLedgerAccountId))
		.andExpect(jsonPath("$.entries[1].journal_id").isNotEmpty())
		.andExpect(jsonPath("$.entries[1].created_at").isNotEmpty())
		.andExpect(jsonPath("$.entries[1].updated_at").isNotEmpty())
		.andExpect(jsonPath("$.created_at").isNotEmpty())
		.andExpect(jsonPath("$.updated_at").isNotEmpty());
		
	}
	
	@Test 
	void testCreateJournal_whenLedgerNotFound_returnNotFound() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String scope = "admin";
		String ledgerId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
		String ledgerAccountId = setUpLedgerAccount().getId().toString();
		String receiverLedgerAccountId = setUpReceiverLedgerAccount().getId().toString();
		
		EntryRequestDto debitRequest = new EntryRequestDto(
											"DEBIT", 
											new BigDecimal("100"),
											ledgerAccountId
											);
		
		EntryRequestDto creditRequest = new EntryRequestDto(
				"CREDIT", 
				new BigDecimal("100"),
				receiverLedgerAccountId
				);
		
		List<EntryRequestDto> entries = new ArrayList<>();
		entries.add(debitRequest);
		entries.add(creditRequest);
		CreateJournalDto request = new CreateJournalDto(
										"Transfer to another wallet", 
										ledgerId, 
										entries);
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", scope)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void testCreateJournal_ifLedgerAccountNotExists_returnNotFound() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String scope = "admin";
		String ledgerId = setUpLedger().getId().toString();
		String ledgerAccountId = setUpLedgerAccount().getId().toString();
		String receiverLedgerAccountId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
		
		EntryRequestDto debitRequest = new EntryRequestDto(
											"DEBIT", 
											new BigDecimal("100"),
											ledgerAccountId
											);
		
		EntryRequestDto creditRequest = new EntryRequestDto(
				"CREDIT", 
				new BigDecimal("100"),
				receiverLedgerAccountId
				);
		
		List<EntryRequestDto> entries = new ArrayList<>();
		entries.add(debitRequest);
		entries.add(creditRequest);
		CreateJournalDto request = new CreateJournalDto(
										"Transfer to another wallet", 
										ledgerId, 
										entries);
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", scope)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger account with id "+ receiverLedgerAccountId + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	Ledger setUpLedger() {
		Ledger ledger = new Ledger();
		ledger.setName("Wallet Ledger");
		ledger.setDescription("");
		
		Ledger saved = ledgerRepository.save(ledger);
		return saved;
	}
	
	LedgerAccount setUpLedgerAccount() {
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setName("Wallet Account");
		ledgerAccount.setDescription("");
		ledgerAccount.setNormalBalance("CREDIT");
		ledgerAccount.setBalance(new BigInteger("100"));
		ledgerAccount.setLedger(setUpLedger());
		LedgerAccount saved = ledgerAccountRepository.save(ledgerAccount);
		return saved;
	}
	
	LedgerAccount setUpReceiverLedgerAccount() {
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setName("Wallet Account 2");
		ledgerAccount.setDescription("");
		ledgerAccount.setNormalBalance("CREDIT");
		ledgerAccount.setBalance(new BigInteger("100"));
		ledgerAccount.setLedger(setUpLedger());
		LedgerAccount saved = ledgerAccountRepository.save(ledgerAccount);
		return saved;
	}
}
