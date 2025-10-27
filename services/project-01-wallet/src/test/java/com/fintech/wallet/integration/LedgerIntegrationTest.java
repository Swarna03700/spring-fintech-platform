package com.fintech.wallet.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fintech.wallet.entity.Ledger;
import com.fintech.wallet.repository.LedgerRepository;


@AutoConfigureMockMvc
public class LedgerIntegrationTest extends AbstractPostgresIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private LedgerRepository ledgerRepository;
	
	Map<String, Object> metadata = new HashMap<>();
	String auth0Id = "auth0|admin-123";
	String email = "admin@example.com";
	
	@Test
	void testCreateLedger() throws Exception {
		
		String request = """
				{
				  "name": "Wallet Ledger",
				  "description": "Tracks all money movements",
				  "metadata": {}
				  }
				""";
		
		mockMvc.perform(post("/api/ledgers")
				.with(jwt().jwt(builder -> builder
						.subject("auth0|admin-123")
						.claim("email", "admin@example.com")
						.claim("scope", "admin")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name").value("Wallet Ledger"))
		.andExpect(jsonPath("$.description").value("Tracks all money movements"))
		.andExpect(jsonPath("$.created_at").isNotEmpty())
		.andExpect(jsonPath("$.updated_at").isNotEmpty());
		
	}
	
	@Test
	void testDeleteLedger_thenReturnNoContentAndRemoveTheResource() throws Exception {
		Ledger ledger = new Ledger();
		ledger.setName("Wallet Ledger");
		ledger.setDescription("Tracks money movements");
		ledger.setMetadata(metadata);
		
		Ledger savedLedger = ledgerRepository.save(ledger);
		UUID id = savedLedger.getId();
		
		assertTrue(ledgerRepository.findById(id).isPresent());
		
		mockMvc.perform(delete("/api/ledgers/{id}", id.toString())
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)
						.claim("scope", "admin"))))
		.andExpect(status().isNoContent());
		
		assertFalse(ledgerRepository.existsById(id));
	}
}
