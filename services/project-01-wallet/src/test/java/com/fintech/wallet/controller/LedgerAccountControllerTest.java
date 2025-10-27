package com.fintech.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateLedgerAccount;
import com.fintech.wallet.dto.LedgerAccountDto;
import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.service.LedgerAccountService;

@WebMvcTest(LedgerAccountController.class)
public class LedgerAccountControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	private LedgerAccountService ledgerAccountService;
	
	String auth0Id = "auth0|admin-123";
	String email = "admin@example.com";
	Map<String, Object> metadata = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();
	
	String name = "Wallet Account";
	String description = "";
	String normalBalance = "credit";
	String ledgerId = "a3b8e1f0-5c7c-4d1b-8e9f-6a7b8c9d0e1f";
	String accountId = "a3b9e1f1-th9c-4d1b-8e9f-6a7b8c9d0e1f";
	@Test
	void testCreateAccountLedger_returnCreated() throws Exception {
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		LedgerAccountDto accountDto = new LedgerAccountDto(
													accountId, 
													name,
													description, 
													metadata, 
													normalBalance, 
													new BigDecimal("100"), 
													ledgerId, 
													Instant.now().toString(), 
													Instant.now().toString());
		when(ledgerAccountService.createLedgerAccount(request)).thenReturn(accountDto);
		String json = mapper.writeValueAsString(request);
		mockMvc.perform(post("/api/ledger_accounts")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.description").value(description))
		.andExpect(jsonPath("$.metadata").value(metadata))
		.andExpect(jsonPath("$.normal_balance").value(normalBalance))
		.andExpect(jsonPath("$.ledger_id").value(ledgerId));
	}
	
	@Test
	void testCreateAccountLedger_whenLedgerNotFound_throwException() throws Exception {
		CreateLedgerAccount request = new CreateLedgerAccount(name, description, metadata, normalBalance, ledgerId);
		when(ledgerAccountService.createLedgerAccount(request)).thenThrow(new LedgerNotFoundException("Ledger with id "+ request.ledgerId()+ " not found"));
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/api/ledger_accounts")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger with id " + ledgerId + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testGetLedgerAccountById_returnOkAndLedgerAccountDto() throws Exception {
		String id = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
		LedgerAccountDto accountDto = new LedgerAccountDto(
				accountId, 
				name,
				description, 
				metadata, 
				normalBalance, 
				new BigDecimal("100"), 
				ledgerId, 
				Instant.now().toString(), 
				Instant.now().toString());
		when(ledgerAccountService.getLedgerAccountById(id)).thenReturn(accountDto);
		
		mockMvc.perform(get("/api/ledger_accounts/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").isNotEmpty())
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.description").value(description))
		.andExpect(jsonPath("$.metadata").value(metadata))
		.andExpect(jsonPath("$.normal_balance").value(normalBalance))
		.andExpect(jsonPath("$.ledger_id").value(ledgerId));
	}
	
	@Test
	void testGetLedgerAccountById_ifNotFound_returnNotFound() throws Exception {
		String id = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
		
		when(ledgerAccountService.getLedgerAccountById(id)).thenThrow(new LedgerAccountNotFoundException("Ledger account with id "+ id + " not found"));
		
		mockMvc.perform(get("/api/ledger_accounts/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email))))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger account with id " + id + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
}
