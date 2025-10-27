package com.fintech.wallet;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.controller.TransactionController;
import com.fintech.wallet.dto.TransactionResponse;
import com.fintech.wallet.dto.TransferRequest;
import com.fintech.wallet.exception.InsufficientBalanceException;
import com.fintech.wallet.exception.InvalidAmountException;
import com.fintech.wallet.exception.MaximumTransactionLimitException;
import com.fintech.wallet.exception.TransactionNotFoundException;
import com.fintech.wallet.exception.UserNotFoundException;
import com.fintech.wallet.service.TransactionService;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
public class TestTransactionController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean(name = "customAuthenticationEntryPoint")
	@Autowired
	AuthenticationEntryPoint customAuthenticationEntryPoint;
	
	@MockitoBean
	JwtDecoder jwtDecoder;
	
	@MockitoBean
	private TransactionService transactionService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Test
	void test_transferSuccess() throws Exception {
		String auth0Id = "auth0|new-user-123";
	    String email = "new.user@example.com";
	    String transactionId = UUID.randomUUID().toString();
	    String timestamp = Instant.now().toString();
		TransferRequest transferRequest = new TransferRequest("john.doe@example.com", new BigDecimal(100));
		TransactionResponse transactionResponse = new TransactionResponse(transactionId, new BigDecimal(100), "SUCCESS", "TRANSFER", "Swarn", "John", timestamp);
		when(transactionService.performTransfer(auth0Id, transferRequest)).thenReturn(transactionResponse);
		
		String json = mapper.writeValueAsString(transferRequest);
		mockMvc.perform(post("/transfers")
							.with(jwt().jwt(builder -> builder
									.subject(auth0Id)
									.claim("email", email)))
							.contentType(MediaType.APPLICATION_JSON)
							.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
				.andExpect(jsonPath("$.status").value("SUCCESS"))
				.andExpect(jsonPath("$.amount").value(new BigDecimal(100)))
				.andExpect(jsonPath("$.senderInfo").value("Swarn"))
				.andExpect(jsonPath("$.receiverInfo").value("John"))
				.andExpect(jsonPath("$.timestamp").value(timestamp.toString()));
	}
	
	@Test
	void test_insufficientBalanceFailure() throws Exception {
		String senderAuth0Id = "auth0|new-user-123";
		String email = "new.user@example.com";
		TransferRequest request = new TransferRequest("john.doe@example.com", new BigDecimal(1000));
		
		// Mock the error scenario
		when(transactionService.performTransfer(senderAuth0Id, request)).thenThrow(new InsufficientBalanceException("Insufficient balance"));
		
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/transfers")
								.with(jwt().jwt(builder -> builder
										.subject(senderAuth0Id)
										.claim("email", email)))
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message").value("Insufficient balance"))
				.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void test_nonexistentReceiver() throws Exception {
		String senderAuth0Id = "auth0|new-user-123";
		String email = "new.user@example.com";
		TransferRequest request = new TransferRequest("john.doe@example.com", new BigDecimal(1000));
		
		when(transactionService.performTransfer(senderAuth0Id, request)).thenThrow(new UserNotFoundException("User does not exist"));
		
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(post("/transfers")
					.with(jwt().jwt(builder -> builder
							.subject(senderAuth0Id)
							.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("User does not exist"))
				.andExpect(jsonPath("$.timestamp").isNotEmpty());

	}
	
	@Test
	void test_invalidAmount() throws Exception {
		String senderAuth0Id = "auth0|new-user-123";
		String email = "new.user@example.com";
		TransferRequest request = new TransferRequest("john.doe@example.com", new BigDecimal(0));
		String json = mapper.writeValueAsString(request);
		
		when(transactionService.performTransfer(senderAuth0Id, request)).thenThrow(new InvalidAmountException("Invalid amount"));
		
		mockMvc.perform(post("/transfers")
						.with(jwt().jwt(builder -> builder
								.subject(senderAuth0Id)
								.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message").value("Invalid amount"))
				.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void test_exceedMaxTransactionLimit() throws Exception {
		String senderAuth0Id = "auth0|new-user-123";
		String email = "new.user@example.com";
		TransferRequest request = new TransferRequest("john.doe@example.com", new BigDecimal(1000000));
		String json = mapper.writeValueAsString(request);
		
		when(transactionService.performTransfer(senderAuth0Id, request)).thenThrow(new MaximumTransactionLimitException("Maximum transaction limit"));
		
		mockMvc.perform(post("/transfers")
							.with(jwt().jwt(builder -> builder
									.subject(senderAuth0Id)
									.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message").value("Maximum transaction limit"))
				.andExpect(jsonPath("$.timestamp").isNotEmpty());
	}
	
	@Test
	void whenUserHasAdminRole_andWantsSeeATransactionDetails_shouldBeAllowed() throws Exception {
		String adminId = "auth0|admin-123";
		String email = "admin@example.com";
		String transactionId = "123";
		mockMvc.perform(get("/admin/transactions/{transactionId}", transactionId)
				.with(jwt()
						.authorities(new SimpleGrantedAuthority("SCOPE_admin"))
						.jwt(builder -> builder
						.subject(adminId)
						.claim("email", email)
						.claim("scope", "admin")
						)))
		.andExpect(status().isOk());
	}
	
	@Test
	void whenRegularAuthenticatedUserTriesToAccessTheAdminEndpoint_thenReturnForbidden() throws Exception {
		String userId = "auth0|regular-user-123";
		String email = "regular_user@example.com";
		String transactionId = "123";
		mockMvc.perform(get("/admin/transactions/{transactionId}", transactionId)
				.with(jwt()
						.jwt(builder -> builder
						.subject(userId)
						.claim("email", email)
						)))
		.andExpect(status().isForbidden());
	}
	
	
	void whenRequestIsMadeWithoutAnyAuthenticationToken_thenReturnUnauthorized() throws Exception {
		String transactionId = "123";
		mockMvc.perform(get("/admin/transactions/{transactionId}", transactionId))
			.andExpect(status().isUnauthorized());
	}
	
	
	void whenAdminRequestsATransactionIdThatDoesNotExist_thenReturnNotFound() throws Exception {
		String userId = "auth0|regular-user-123";
		String email = "regular_user@example.com";
		String transactionId = "123";
		
//		when(transactionService.getTransactionById(transactionId)).thenThrow(new TransactionNotFoundException("Transaction not found"));
		mockMvc.perform(get("/admin/transactions/{transactionId}", transactionId)
				.with(jwt().jwt(builder -> builder
						.subject(userId)
						.claim("email", email)
						)))
		.andExpect(status().isNotFound());
	}

}
