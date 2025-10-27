package com.fintech.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateLedgerRequest;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.service.LedgerService;

@WebMvcTest(LedgerController.class)
public class LedgerControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private LedgerService ledgerService;
	
	String auth0Id = "auth0|admin-123";
	String email = "admin@example.com";
	String name = "Wallet Ledger";
	String description = "Tracks money movements";
	Map<String, Object> metadata = new HashMap<>();
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test
	void testCreateLedger() throws Exception {
		
		String json = """
				{
				  "name": "Business Ledger",
				  "description": "The ledger tracks money movements",
				  "metadata": {}
				}
				""";
		mockMvc.perform(post("/api/ledgers")
				.with(jwt().jwt(builder -> builder
						.subject("auth0|admin-123")
						))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated());
	
	}
	
	@Test
	void testGetLedgerById() throws Exception {
		
		String id = "123";
		mockMvc.perform(get("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						)))
		
		.andExpect(status().isOk());
	}
	
	@Test
	void testGetLedgerById_throwsException_ifLedgerDoesNotExist() throws Exception {
		String id = "123";
		when(ledgerService.getLedgerById(id)).thenThrow(new LedgerNotFoundException("Ledger with id " + id + " not found"));
		
		mockMvc.perform(get("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger with id " + id + " not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testUpdateLedgerById() throws Exception {
		String id = "123";
		CreateLedgerRequest request = new CreateLedgerRequest(name, description, metadata);
		String json = mapper.writeValueAsString(request);
		mockMvc.perform(patch("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject("auth0|admin-123")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk());
	}
	
	@Test
	void testUpdateLedgerById_ifNotExists_throwsException() throws Exception {
		String id = "123";
		CreateLedgerRequest request = new CreateLedgerRequest(name, description, metadata);
		String json = mapper.writeValueAsString(request);
		when(ledgerService.updateLedgerById(id, request)).thenThrow(new LedgerNotFoundException("Ledger with id "+ id +" not found"));
		
		mockMvc.perform(patch("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject("auth0|admin-123")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("NOT_FOUND"))
		.andExpect(jsonPath("$.message").value("Ledger with id "+ id +" not found"))
		.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void testDeleteLedgerById() throws Exception {
		String id = "123";
		
		mockMvc.perform(delete("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id))))
		.andExpect(status().isNoContent());
	}
	
	@Test
	void testDeleteLedgerById_ifNotExists_thenThrowException() throws Exception {
		String id = "123";
		
		when(ledgerService.deleteLedgerById(id)).thenThrow(new LedgerNotFoundException("Ledger with id "+ id + " not found"));
		
		mockMvc.perform(delete("/api/ledgers/{id}", id)
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id))))
		.andExpect(status().isNotFound());
	}
}
