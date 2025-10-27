package com.fintech.wallet;

// Import everything you need, including the crucial one for jwt()
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt; // <-- THE FIX IS HERE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.controller.ProfileController;
import com.fintech.wallet.controller.WalletController;
import com.fintech.wallet.dto.CompleteProfileRequest;
import com.fintech.wallet.dto.UserResponse;
import com.fintech.wallet.dto.WalletResponse;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.exception.WalletNotFoundException;
import com.fintech.wallet.service.UserService;
import com.fintech.wallet.service.WalletService;

@WebMvcTest({ProfileController.class, WalletController.class})
public class TestControllers {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean // Creates a "fake" version of UserService for our test
	private UserService userService;
	
	@MockitoBean // Creates a "fake" version of WalletService
	private WalletService walletService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Test
	void whenGetMyProfile_butProfileIsNotComplete_shouldReturnIncompleteProfileData() throws Exception {
		String auth0Id = "auth0|john-doe-123";
	    String email = "john.doe@example.com";
	    String firstName = null;
	    String lastName = null;
	    boolean isProfileComplete = false;
	    UserResponse expectedResponse = new UserResponse(auth0Id, email, firstName, lastName, isProfileComplete);
	    
	    when(userService.findOrCreateUser(any(Jwt.class))).thenReturn(expectedResponse);
	    
	    mockMvc.perform(get("/me")
	    		.with(jwt().jwt(jwt -> jwt
	    				.subject(auth0Id)
	    				.claim("email", email)
	    				)))
	    
	    		.andExpect(status().isOk())
	    		.andExpect(jsonPath("$.userId").value(auth0Id))
	    		.andExpect(jsonPath("$.email").value(email))
	    		.andExpect(jsonPath("$.firstName").isEmpty())
	    		.andExpect(jsonPath("$.lastName").isEmpty())
	    		.andExpect(jsonPath("$.isProfileComplete").value(isProfileComplete));
	}
	@Test
	void whenGetMyProfile_withNewUser_shouldReturnUserProfile() throws Exception {
	    // --- ACT 1: SETUP ---
	    // We define the data we'll be working with.
	    String auth0Id = "auth0|john-doe-123";
	    String email = "john.doe@example.com";
	    String firstName = "John";
	    String lastName = "Doe";
	    boolean isProfileComplete = true;
	    UserResponse expectedResponse = new UserResponse(auth0Id, email, firstName, lastName, isProfileComplete);

	    // We program our fake service.
	    // When findOrCreateUser is called, it MUST return our expectedResponse.
	    when(userService.findOrCreateUser(any(Jwt.class))).thenReturn(expectedResponse);

	    // --- ACT 2: ACTION ---
	    // We use MockMvc to perform a fake web request to our endpoint.
	    mockMvc.perform(get("/me")
	            // We attach a fake JWT to pretend we are a logged-in user.
	            .with(jwt().jwt(builder -> builder
	                    .subject(auth0Id)
	                    .claim("email", email)
	            )))
	    
	    // --- ACT 3: VERIFICATION ---
	    // We check if the response from the controller is what we expect.
	            .andExpect(status().isOk()) // Was the HTTP status 200 OK?
	            .andExpect(jsonPath("$.userId").value(auth0Id)) // Does the 'id' in the JSON match?
	            .andExpect(jsonPath("$.email").value(email)) // Does the 'email' in the JSON match?
	            .andExpect(jsonPath("$.firstName").value(firstName)) // Does the 'role' in the JSON match?
	            .andExpect(jsonPath("$.lastName").value(lastName))
	            .andExpect(jsonPath("$.isProfileComplete").value(isProfileComplete));
	}
	
	@Test
	void whenGetMyWallet_withExistingUserButProfileNotCompleted_shouldReturnWalletDetails() throws Exception {
	    // 1. Setup
	    String auth0Id = "auth0|existing-user-456";
	    String email = "existing_user@example.com";
	    boolean isProfileComplete = false;
	    
	    // Create a mock User 
	    UserEntity existingUser = new UserEntity(auth0Id, email, isProfileComplete); 
	    
	    String walletId = UUID.randomUUID().toString();
	    BigDecimal balance = new BigDecimal("100");
	    // Create a mock Wallet
	    Wallet wallet = new Wallet(walletId, existingUser, balance, "INR", false); 
	    
	    WalletResponse walletResponse = new WalletResponse(wallet.getWalletId(), wallet.getBalance(), wallet.getCurrency(), wallet.getIsActive());
	    // Mock the service calls
	    when(userService.getOrCreateUserEntity(any(Jwt.class))).thenReturn(existingUser);
	    when(walletService.getWalletForUser(existingUser)).thenReturn(walletResponse);

	    // 2. Act & Assert
	    mockMvc.perform(get("/wallet")
	            .with(jwt().jwt(builder -> builder.subject(auth0Id))))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.walletId").value(walletId.toString()))
	            .andExpect(jsonPath("$.balance").value(balance))
	    		.andExpect(jsonPath("$.currency").value("INR"))
	    		.andExpect(jsonPath("$.isActive").value(false));
	}

	@Test
	void whenGetMyWallet_butWalletNotFound_shouldReturn404() throws Exception {
	    // 1. Setup
	    UserEntity userWithoutWallet = new UserEntity();
//	    userWithoutWallet.setAuth0Id(3L);

	    // Mock the services
	    when(userService.getOrCreateUserEntity(any(Jwt.class))).thenReturn(userWithoutWallet);
	    when(walletService.getWalletForUser(userWithoutWallet))
	            .thenThrow(new WalletNotFoundException("Wallet not found for user"));

	    // 2. Act & Assert
	    mockMvc.perform(get("/wallet")
	            .with(jwt())) // A default JWT is enough for this test
	            .andExpect(status().isNotFound());
	}
	
	@Test
	void whenPatchCompleteProfile_isSuccessful_shouldReturnNoContent() throws Exception {
		String auth0Id = "auth0|john-doe-123";
		String email = "john.doe@example.com";
		String firstName = "John";
		String lastName = "Doe";
		LocalDate dateOfBirth = LocalDate.of(2000, 10, 14);
		boolean isProfileComplete = true;
		CompleteProfileRequest request = new CompleteProfileRequest(firstName, lastName, dateOfBirth);
		
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(patch("/me")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)
						.claim("email", email)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNoContent());
	}
}