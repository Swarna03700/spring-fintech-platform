package com.fintech.wallet;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import com.fintech.wallet.dto.CompleteProfileRequest;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;
import com.fintech.wallet.service.UserService;


@ExtendWith(MockitoExtension.class)
public class TestUserService {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private WalletRepository walletRepository;
	
	@Captor
	private ArgumentCaptor<UserEntity> userCaptor;
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private Jwt mockJwt;
	
	@Test
	void whenUserDoesNotExist_thenReturnCompleteProfile() {
		String auth0Id = "auth0|john-doe-123";
		String email = "john.doe@example.com";
		
		// Configure the mock Jwt object
		// Tell the mock what to return when its methods are called by your service
		when(mockJwt.getSubject()).thenReturn(auth0Id);
		when(mockJwt.getClaimAsString("https://wallet-app/email")).thenReturn(email);
		
		// Mock the repository to simulate the user not being found
		when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());
		
		when(userRepository.save(any(UserEntity.class)))
	        .thenAnswer(invocation -> invocation.getArgument(0));

	    // Mock wallet save to avoid nulls
	    when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		
		userService.findOrCreateUser(mockJwt);
		
		// Verify the repository was called correctly
		verify(userRepository).save(userCaptor.capture());
		
		UserEntity savedUser = userCaptor.getValue();
		assertNotNull(savedUser, "savedUser should not be null");
		
		assertEquals(auth0Id, savedUser.getAuth0Id());
		assertEquals(email, savedUser.getEmail());
	}
	
	@Test
	void whenUserAlreadyExists_thenReturnProfile() {
		String auth0Id = "auth0|john-doe-123";
		
		UserEntity existingUser = new UserEntity(auth0Id, "john.doe@example.com", true, "John", "Doe");
		
		when(mockJwt.getSubject()).thenReturn(auth0Id);
		
		when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(existingUser));
		
		
		userService.findOrCreateUser(mockJwt);
		
		verify(userRepository, never()).save(any(UserEntity.class));
	}
	
	@Test
	void whenUserSendsCompleteProfileRequest_thenProfileShouldBeUpdated() {
		String auth0Id = "auth0|john-doe-123";
		LocalDate dateOfBirth = LocalDate.of(2000, 10, 14);
		CompleteProfileRequest request = new CompleteProfileRequest("John", "Doe", dateOfBirth);
		UserEntity existingUser = new UserEntity(auth0Id, "john_doe@example.com", false);
		
		when(mockJwt.getSubject()).thenReturn(auth0Id);
		when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(existingUser));
		
		userService.completeUserProfile(request, mockJwt);
		
//		verify(userRepository, never()).save(any(UserEntity.class));
		// Verify the repository was called correctly
		verify(userRepository).save(userCaptor.capture());
				
		UserEntity savedUser = userCaptor.getValue();
				
		assertEquals(auth0Id, savedUser.getAuth0Id());
		assertEquals("John", savedUser.getFirstName());
	}
	
}
