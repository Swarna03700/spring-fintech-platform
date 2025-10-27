package com.fintech.wallet.integration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateLedgerAccount;
import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.entity.LedgerAccount;
import com.fintech.wallet.repository.LedgerAccountRepository;
import com.fintech.wallet.repository.LedgerRepository;

@AutoConfigureMockMvc
public class LedgerAccountIntegrationTest extends AbstractPostgresIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private LedgerRepository ledgerRepository;
	
	@Autowired
	private LedgerAccountRepository ledgerAccountRepository;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	String name = "Wallet Account";
	String description = "";
	Map<String, Object> metadata = new HashMap<>();
	String normalBalance = "credit";
	String accountId = "a3b9e1f1-th9c-4d1b-8e9f-6a7b8c9d0e1f";
	
	String auth0Id = "auth0|admin-123";
	String email = "admin@example.com";
	
	@Test
	void testCreateLedgerAccount_returnCreated() throws Exception {
		String ledgerId = setUpLedger();
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		String jsonRequest = mapper.writeValueAsString(request);
		mockMvc.perform(post("/api/ledger_accounts")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", "admin  ")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.description").value(description))
		.andExpect(jsonPath("$.metadata").value(metadata))
		.andExpect(jsonPath("$.normal_balance").value(normalBalance))
		.andExpect(jsonPath("$.balance").value(new BigDecimal("0.0")))
		.andExpect(jsonPath("$.ledger_id").isNotEmpty())
		.andExpect(jsonPath("$.created_at").isNotEmpty())
		.andExpect(jsonPath("$.updated_at").isNotEmpty());
		
	}
	
	@Test
	void testCreateLedgerAccount_whenLedgerNotFound_returnNotFound() throws Exception {
		String ledgerId = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		String jsonRequest = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/api/ledger_accounts")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", "admin")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger with id "+ ledgerId +" not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testGetLedgerAccountById_returnOk() throws Exception {
		String id = setUpLedgerAccount();
		mockMvc.perform(get("/api/ledger_accounts/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", "admin  "))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.description").value(description))
		.andExpect(jsonPath("$.metadata").value(metadata))
		.andExpect(jsonPath("$.normal_balance").value(normalBalance))
		.andExpect(jsonPath("$.balance").value(new BigDecimal("0.0")))
		.andExpect(jsonPath("$.ledger_id").isNotEmpty())
		.andExpect(jsonPath("$.created_at").isNotEmpty())
		.andExpect(jsonPath("$.updated_at").isNotEmpty());
	}
	
	@Test
	void testGetLedgerAccountById_ifNotExists_returnNotFound() throws Exception {
		String id = "b8d3f8f0-3b7c-4a1e-8f9b-6d6a1f8c7e4d";
		
		mockMvc.perform(get("/api/ledger_accounts/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", "admin"))))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger account with id "+ id + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	String setUpLedger() {
		Ledger ledger = new Ledger();
		ledger.setName("Wallet Ledger");
		ledger.setDescription(null);
		ledger.setMetadata(metadata);
		
		Ledger saved = ledgerRepository.saveAndFlush(ledger);
		
		return saved.getId().toString();
	}
	
	String setUpLedgerAccount() {
		String id = setUpLedger();
		
		Ledger ledger = ledgerRepository.findById(UUID.fromString(id))
				.orElseThrow();
		
		LedgerAccount ledgerAccount = new LedgerAccount();
		ledgerAccount.setName("Wallet Account");
		ledgerAccount.setDescription("");
		ledgerAccount.setMetadata(metadata);
		ledgerAccount.setBalance(BigInteger.ZERO);
		ledgerAccount.setNormalBalance(normalBalance);
		ledgerAccount.setLedger(ledger);
		
		LedgerAccount saved = ledgerAccountRepository.save(ledgerAccount);
		
		return saved.getId().toString();
	}
}
