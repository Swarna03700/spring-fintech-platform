package com.fintech.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.service.EntryService;

@WebMvcTest(EntryController.class)
public class EntryControllerTest {
	
	@MockitoBean
	private EntryService entryService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void testGetEntryById_returnOkAndEntryDto() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String id = "cc0a579a-1f6b-4a52-94c1-541689e90c28";
		String ledgerAccountId = "90713ced-b89f-4adb-ad38-bb6e4a1a4544";
		String journalId = "b492ba41-09b2-4a6f-b00e-6787609484f5";
		EntryDto entryDto = new EntryDto(
								id,
								new BigDecimal("100.0"),
								"POSTED",
								"DEBIT",
								ledgerAccountId,
								journalId,
								Instant.now().toString(),
								Instant.now().toString()
								);
		when(entryService.getEntry(id)).thenReturn(entryDto);
		
		mockMvc.perform(get("/api/ledger_entries/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(entryDto.id()))
		.andExpect(jsonPath("$.amount").value(entryDto.amount()))
		.andExpect(jsonPath("$.status").value(entryDto.status()))
		.andExpect(jsonPath("$.direction").value(entryDto.direction()))
		.andExpect(jsonPath("$.ledger_account_id").value(entryDto.ledgerAccountId()))
		.andExpect(jsonPath("$.journal_id").value(entryDto.journalId()))
		.andExpect(jsonPath("$.created_at").value(entryDto.createdAt()))
		.andExpect(jsonPath("$.updated_at").value(entryDto.updatedAt()));
	}
	
	@Test
	void testGetAllEntries_returnOkAndListOfEntryDtos() throws Exception {
		String auth0Id = "auth0|admin-123";
		String email = "admin@example.com";
		String id = "cc0a579a-1f6b-4a52-94c1-541689e90c28";
		String ledgerAccountId = "90713ced-b89f-4adb-ad38-bb6e4a1a4544";
		String journalId = "b492ba41-09b2-4a6f-b00e-6787609484f5";
		
		List<EntryDto> entries = new ArrayList<>();
		EntryDto entryDto = new EntryDto(
				id,
				new BigDecimal("100.0"),
				"POSTED",
				"DEBIT",
				ledgerAccountId,
				journalId,
				Instant.now().toString(),
				Instant.now().toString()
				);
		entries.add(entryDto);
		when(entryService.getAllEntries()).thenReturn(entries);
		mockMvc.perform(get("/api/ledger_entries")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].id").value(id))
		.andExpect(jsonPath("$.[0].amount").value(new BigDecimal("100.0")))
		.andExpect(jsonPath("$.[0].status").value("POSTED"))
		.andExpect(jsonPath("$.[0].direction").value("DEBIT"))
		.andExpect(jsonPath("$.[0].ledger_account_id").value(ledgerAccountId))
		.andExpect(jsonPath("$.[0].journal_id").value(journalId))
		.andExpect(jsonPath("$.[0].created_at").isNotEmpty())
		.andExpect(jsonPath("$.[0].updated_at").isNotEmpty());
	}
}
