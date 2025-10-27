package com.fintech.wallet;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserProvisioningIntegrationTest {
	
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
	MockMvc mockMvc;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	WalletRepository walletRepository;
	
	@BeforeEach
	void setUp() {
		walletRepository.deleteAll();;
		userRepository.deleteAll();
	}
	
	@Test
	void whenFirstTimeUserAccessesApi_thenUserAndWalletAreCreated() throws Exception {
		String newUserAuth0Id = "auth0|new-user-123";
		String newUserEmail = "new.user@example.com";
		
		assertFalse(userRepository.findByAuth0Id(newUserAuth0Id).isPresent());
		
		mockMvc.perform(get("/me")
						.with(jwt().jwt(jwt -> jwt
								.subject(newUserAuth0Id)
								.claim("https://wallet-app/email", newUserEmail)
								)))
				.andExpect(status().isOk());
		
		Optional<UserEntity> user = userRepository.findByAuth0Id(newUserAuth0Id);
		
		assertTrue(user.isPresent(), "User should have been created in the database");
		assertEquals(newUserEmail, user.get().getEmail());
		
		
		Optional<Wallet> wallet = walletRepository.findByUser(user.get());
		
		assertTrue(wallet.isPresent(), "Wallet should have been created for the new user");
		assertEquals(50, wallet.get().getBalance().intValue(), "Wallet should be created with a bonus balance of 50");
	}
}	