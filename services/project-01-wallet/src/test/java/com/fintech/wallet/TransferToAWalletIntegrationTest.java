package com.fintech.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.dto.TransferRequest;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class TransferToAWalletIntegrationTest {
	
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
	
	private UserEntity sender;
	private UserEntity receiver;
	
	private Wallet senderWallet;
	private Wallet receiverWallet;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	void setUp() {
		sender = new UserEntity("auth0|sender-123", "sender@example.com", true);
//		sender.setEmail("sender@example.com");
//		sender.setAuth0Id("auth0|sender-123");
//		sender.setRole("USER");
		
		userRepository.save(sender);
		
		receiver = new UserEntity("auth0|receiver-123", "receiver@example.com", true);
//		receiver.setEmail("receiver@example.com");
//		receiver.setAuth0Id("auth0|receiver-123");
//		receiver.setRole("USER");
		
		userRepository.save(receiver);
		
		senderWallet = new Wallet("272geue8202e", sender, new BigDecimal("100"), "INR", true);
//		senderWallet.setBalance(new BigDecimal(100));
//		senderWallet.setCurrency("INR");
//		senderWallet.setUser(sender);
		
		walletRepository.save(senderWallet);
		
		receiverWallet = new Wallet("26e8eh2h92", receiver, new BigDecimal("200"), "INR", true);
//		receiverWallet.setBalance(new BigDecimal(200));
//		receiverWallet.setCurrency("INR");
//		receiverWallet.setUser(receiver);
		
		walletRepository.save(receiverWallet);
	}
	
	@Test
	void whenUserWantsToTransferBalance_thenTransactionIsCreated() throws Exception {
		String senderAuth0Id = "auth0|sender-123";
		String receiverEmail = "receiver@example.com";
		TransferRequest transferRequest = new TransferRequest(receiverEmail, new BigDecimal("50"));
		
		String json = mapper.writeValueAsString(transferRequest);
		
		mockMvc.perform(post("/transfers")
								.with(jwt().jwt(jwt -> jwt
										.subject(senderAuth0Id)))
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.transactionId").isNotEmpty())
				.andExpect(jsonPath("$.status").value("SUCCESS"))
				.andExpect(jsonPath("$.amount").value(50))
				.andExpect(jsonPath("$.type").value("TRANSFER"))
				.andExpect(jsonPath("$.senderInfo").value("sender@example.com"))
				.andExpect(jsonPath("$.receiverInfo").value("receiver@example.com"))
				.andExpect(jsonPath("$.timestamp").isNotEmpty());
		
		Optional<Wallet> walletOfSender = walletRepository.findByUser(sender);
		assertEquals(50, walletOfSender.get().getBalance().intValue());
		
		Optional<Wallet> walletOfReceiver = walletRepository.findByUser(receiver);
		assertEquals(250, walletOfReceiver.get().getBalance().intValue());
	}
}
