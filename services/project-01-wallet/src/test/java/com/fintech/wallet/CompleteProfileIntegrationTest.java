package com.fintech.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.CompleteProfileRequest;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CompleteProfileIntegrationTest {
	
	@Container
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6-alpine")
										.withDatabaseName("testdb")
										.withUsername("test")
										.withPassword("test");
	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	void whenUserExists_andSendsCompleteProfileRequest_shouldUpdateProfile() throws Exception {
		// ------ SETUP PHASE --------
		String auth0Id = "auth0|john-doe-123";
		String email = "john_doe@example.com";
		// Create the user entity that should already exist in the database
		UserEntity existingUser = new UserEntity(auth0Id, email, false);
		
		// Save the user to the database before the test runs
		userRepository.save(existingUser);
		
		// Create a wallet
		Wallet existingWallet = new Wallet(UUID.randomUUID().toString(), existingUser, new BigDecimal("100"), "INR", false);
		
		walletRepository.save(existingWallet);
		
		// ----- ACTION PHASE ------
		String firstName = "John";
		String lastName = "Doe";
		LocalDate dateOfBirth = LocalDate.of(2000, 10, 14);
		CompleteProfileRequest request = new CompleteProfileRequest(firstName, lastName, dateOfBirth);
		String json = mapper.writeValueAsString(request);
		
		mockMvc.perform(patch("/me")
				.with(jwt().jwt(builder -> builder
						.subject(auth0Id)))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNoContent());
		
		// ------- ASSERTION PHASE ------
		UserEntity user = userRepository.findByAuth0Id(auth0Id)
				.orElseThrow();
		Wallet wallet = walletRepository.findByUser(user)
				.orElseThrow();
		assertEquals(firstName, user.getFirstName());
		assertEquals(lastName, user.getLastName());
		assertEquals(dateOfBirth, user.getDateOfBirth());
		assertTrue(user.getIsProfileComplete());
		assertTrue(wallet.getIsActive());
	}
}
