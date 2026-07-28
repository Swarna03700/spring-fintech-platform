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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CreateLedgerRequest;
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
	
	private ObjectMapper mapper = new ObjectMapper();
	
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
	void testCreateLedger_ifDescriptionCharacterLimitExceeds_returnUnprocessableEntityResponse() throws Exception {
		CreateLedgerRequest request = new CreateLedgerRequest(
				"Wallet Ledger", 
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
				+ "Aenean commodo ligula eget dolor. Aenean massa. Cum sociis "
				+ "natoque penatibus et magnis dis parturient montes, nascetur"
				+ " ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, "
				+ "pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, "
				+ "fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, "
				+ "rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum"
				+ " felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. "
				+ "Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."
				+ " Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. "
				+ "Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus "
				+ "viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. "
				+ "Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi.",
				metadata
				);
        String json = mapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/ledgers")
				.with(jwt().jwt(builder -> builder
						.subject("auth0|admin-123")
						.claim("email", "admin@example.com")
						.claim("scope", "admin")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
        .andExpect(status().isUnprocessableEntity());
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
