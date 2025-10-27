package com.fintech.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateJournalDto;
import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.dto.EntryRequestDto;
import com.fintech.wallet.dto.JournalDto;
import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.service.JournalService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.closeTo;

@WebMvcTest(JournalController.class)
public class JournalControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private JournalService journalService;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	String auth0Id = "auth0|admin-123";
	String email = "admin@example.com";
	String description = "Transfer from Madhab to Krishna";
	String ledgerId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
	String ledgerAccountId = "b8d3f8f0-3b7c-4a1e-8f9b-6d6a1f8c7e4d";
	String receiverLedgerAccountId = "b8d3f8f0-3b7c-4a1e-8f9b-6d6a1f8c7e4d";
	String journalId = "123e4567-e89b-12d3-a456-426614174000";
	String entryId = "98765432-10ab-cdef-0123-456789abcdef";
	String creditEntryId = "98765432-10ab-cdef-0123-456789abcdef";
	String status = "POSTED";
	
	@Test
	void testCreateJournal_returnCreated() throws Exception {
		
		CreateJournalDto request = setUpRequest();
		EntryDto debitEntryDto = new EntryDto(
										entryId,
										new BigDecimal("100.0"),
										status, 
										"DEBIT", 
										ledgerAccountId, 
										journalId, 
										Instant.now().toString(), 
										Instant.now().toString());
		EntryDto creditEntryDto = new EntryDto(
				entryId,
				new BigDecimal("100.0"),
				status, 
				"CREDIT", 
				ledgerAccountId, 
				journalId, 
				Instant.now().toString(), 
				Instant.now().toString());
		
		List<EntryDto> entryDtos = new ArrayList<>();
		entryDtos.add(debitEntryDto);
		entryDtos.add(creditEntryDto);
		JournalDto journalDto = new JournalDto(
										journalId,
										description,
										status,
										ledgerId, 
										entryDtos,
										Instant.now().toString(),
										Instant.now().toString());
		String jsonString = mapper.writeValueAsString(request);
		
		when(journalService.createJournal(request)).thenReturn(journalDto);
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(journalDto.id()))
		.andExpect(jsonPath("$.description").value(journalDto.description()))
		.andExpect(jsonPath("$.status").value(journalDto.status()))
		.andExpect(jsonPath("$.ledger_id").value(journalDto.ledgerId()))
		.andExpect(jsonPath("$.entries", hasSize(2)))
		.andExpect(jsonPath("$.entries[0].id", is(debitEntryDto.id())))
		.andExpect(jsonPath("$.entries[0].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[0].status", is(debitEntryDto.status())))
		.andExpect(jsonPath("$.entries[0].direction", is(debitEntryDto.direction())))
		.andExpect(jsonPath("$.entries[0].ledger_account_id", is(debitEntryDto.ledgerAccountId())))
		.andExpect(jsonPath("$.entries[0].journal_id", is(debitEntryDto.journalId())))
		.andExpect(jsonPath("$.entries[0].created_at", is(debitEntryDto.createdAt())))
		.andExpect(jsonPath("$.entries[0].updated_at", is(debitEntryDto.updatedAt())))
		.andExpect(jsonPath("$.entries[1].id", is(creditEntryDto.id())))
		.andExpect(jsonPath("$.entries[1].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[1].status", is(creditEntryDto.status())))
		.andExpect(jsonPath("$.entries[1].direction", is(creditEntryDto.direction())))
		.andExpect(jsonPath("$.entries[1].ledger_account_id", is(creditEntryDto.ledgerAccountId())))
		.andExpect(jsonPath("$.entries[1].journal_id", is(creditEntryDto.journalId())))
		.andExpect(jsonPath("$.entries[1].created_at", is(creditEntryDto.createdAt())))
		.andExpect(jsonPath("$.entries[1].updated_at", is(creditEntryDto.updatedAt())))
		.andExpect(jsonPath("$.created_at").value(journalDto.createdAt()))
		.andExpect(jsonPath("$.updated_at").value(journalDto.updatedAt()));
		
	}
	
	@Test
	void testCreateJournal_ifLedgerNotFound_returnNotFound() throws Exception{
		
		CreateJournalDto request = setUpRequest();
		String jsonString = mapper.writeValueAsString(request);
		
		when(journalService.createJournal(request)).thenThrow(new LedgerNotFoundException("Ledger with id "+ ledgerId + " not found"));
		
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger with id "+ ledgerId + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testCreateJournal_ifLedgerAccountNotExist_returnNotFound() throws Exception {
		CreateJournalDto request = setUpRequest();
		String jsonString = mapper.writeValueAsString(request);
		when(journalService.createJournal(request)).thenThrow(new LedgerAccountNotFoundException("Ledger account with id "+ ledgerAccountId + " not found"));
		
		mockMvc.perform(post("/api/journals")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger account with id " + ledgerAccountId + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testGetJournalById_returnOkAndJournalDto() throws Exception {
		String journalId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
		
		EntryDto debitEntryDto = new EntryDto(
										entryId, 
										new BigDecimal("100.0"), 
										status, 
										"DEBIT", 
										ledgerAccountId, 
										journalId, 
										Instant.now().toString(), 
										Instant.now().toString());
		EntryDto creditEntryDto = new EntryDto(
										creditEntryId,
										new BigDecimal("100.0"),
										status,
										"CREDIT",
										receiverLedgerAccountId,
										journalId,
										Instant.now().toString(),
										Instant.now().toString()
										);
		List<EntryDto> entryDtos = new ArrayList<>();
		entryDtos.add(debitEntryDto);
		entryDtos.add(creditEntryDto);
		
		JournalDto journalDto =  new JournalDto(
										journalId,
										description,
										status,
										ledgerId,
										entryDtos,
										Instant.now().toString(),
										Instant.now().toString()
										);
		when(journalService.getJournalById(journalId)).thenReturn(journalDto);
		mockMvc.perform(get("/api/journals/{id}", journalId)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(journalDto.id()))
		.andExpect(jsonPath("$.description").value(journalDto.description()))
		.andExpect(jsonPath("$.status").value(journalDto.status()))
		.andExpect(jsonPath("$.ledger_id").value(journalDto.ledgerId()))
		.andExpect(jsonPath("$.entries", hasSize(2)))
		.andExpect(jsonPath("$.entries[0].id", is(debitEntryDto.id())))
		.andExpect(jsonPath("$.entries[0].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[0].status", is(debitEntryDto.status())))
		.andExpect(jsonPath("$.entries[0].direction", is(debitEntryDto.direction())))
		.andExpect(jsonPath("$.entries[0].ledger_account_id", is(debitEntryDto.ledgerAccountId())))
		.andExpect(jsonPath("$.entries[0].journal_id", is(debitEntryDto.journalId())))
		.andExpect(jsonPath("$.entries[0].created_at", is(debitEntryDto.createdAt())))
		.andExpect(jsonPath("$.entries[0].updated_at", is(debitEntryDto.updatedAt())))
		.andExpect(jsonPath("$.entries[1].id", is(creditEntryDto.id())))
		.andExpect(jsonPath("$.entries[1].amount", closeTo(100.0, 0.0)))
		.andExpect(jsonPath("$.entries[1].status", is(creditEntryDto.status())))
		.andExpect(jsonPath("$.entries[1].direction", is(creditEntryDto.direction())))
		.andExpect(jsonPath("$.entries[1].ledger_account_id", is(creditEntryDto.ledgerAccountId())))
		.andExpect(jsonPath("$.entries[1].journal_id", is(creditEntryDto.journalId())))
		.andExpect(jsonPath("$.entries[1].created_at", is(creditEntryDto.createdAt())))
		.andExpect(jsonPath("$.entries[1].updated_at", is(creditEntryDto.updatedAt())))
		.andExpect(jsonPath("$.created_at").value(journalDto.createdAt()))
		.andExpect(jsonPath("$.updated_at").value(journalDto.updatedAt()));
	}
	
	CreateJournalDto setUpRequest() {
		List<EntryRequestDto> entries = new ArrayList<>();
		EntryRequestDto debitEntry = new EntryRequestDto("DEBIT", new BigDecimal("100"), ledgerAccountId);
		EntryRequestDto creditEntry = new EntryRequestDto("CREDIT", new BigDecimal("100"), ledgerAccountId);
		entries.add(debitEntry);
		entries.add(creditEntry);
		CreateJournalDto request = new CreateJournalDto(description, ledgerId, entries);
		
		return request;
	}
}
