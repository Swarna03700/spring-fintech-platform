package com.fintech.wallet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ViewWalletDetailsIntegrationTest {
	
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
	
	private UserEntity testUser;
	
	@BeforeEach
	void setUp() {
		testUser = new UserEntity("auth0|test-user-123", "test.user@example.com", false);
//		testUser.setEmail("test.user@example.com");
//		testUser.setAuth0Id("auth0|test-user-123");
//		testUser.setRole("USER");
		
		userRepository.save(testUser);
		
		Wallet wallet = new Wallet(UUID.randomUUID().toString(), testUser, new BigDecimal("125.15"), "INR", false);
		
		walletRepository.save(wallet);
	}
	
	@Test
	void whenUserWantsToViewTheirWalletBalance_thenReturnWalletResponse() throws Exception {
		String newUserAuth0Id = "auth0|test-user-123";
		String newUserEmail = "test.user@example.com";
		
		mockMvc.perform(get("/wallet")
							.with(jwt().jwt(jwt -> jwt
									.subject(newUserAuth0Id)
									.claim("https://wallet-app/email", newUserEmail)
									)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.balance").value(125.15))
				.andExpect(jsonPath("$.currency").value("INR"))
				.andExpect(jsonPath("$.isActive").value(false));
	}
}
