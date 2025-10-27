package com.fintech.wallet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fintech.wallet.integration.AbstractPostgresIntegrationTest;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserIntegrationPostgresTest extends AbstractPostgresIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;

    
    void registerUser_createsWallet() throws Exception {
        String requestJson = """
            { "email": "alice@example.com", "password": "alice123" }
        """;

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.role").value("USER"));
    }
    
    
    void getWallet_success() throws Exception {
        // Register user
        String requestJson = """
            { "email": "bob@example.com", "password": "bob12345" }
        """;

        String userId = mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String id = JsonPath.read(userId, "$.id"); // parse out the id

        // Fetch wallet
        mockMvc.perform(get("/api/v1/users/{id}/wallet", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(0.0F))
            .andExpect(jsonPath("$.currency").value("INR"));
    }
}
