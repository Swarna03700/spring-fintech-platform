package com.fintech.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.fintech.wallet.dto.CompleteProfileRequest;
import com.fintech.wallet.dto.UserResponse;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.exception.UserNotFoundException;
import com.fintech.wallet.exception.WalletNotFoundException;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@Service
public class UserService {

    private final WalletRepository walletRepository;
	
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository, WalletRepository walletRepository) {
		this.userRepository = userRepository;
		this.walletRepository = walletRepository;
	}
	
	public UserResponse findOrCreateUser(Jwt jwt) {
		UserEntity user = getOrCreateUserEntity(jwt);
		return new UserResponse(user.getAuth0Id(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getIsProfileComplete());
	}
	
	public UserEntity getOrCreateUserEntity(Jwt jwt) {
        String auth0Id = jwt.getSubject();

        return userRepository.findByAuth0Id(auth0Id)
            .orElseGet(() -> {
                // This block runs ONLY for a new user
                String email = jwt.getClaimAsString("https://wallet-app/email");
                UserEntity newUser = new UserEntity(auth0Id, email, false);
                UserEntity savedUser = userRepository.save(newUser);

                // Create the corresponding Wallet
                Wallet newWallet = new Wallet(UUID.randomUUID().toString(), savedUser, new BigDecimal("50"), "INR", false);
//                newWallet.setUser(savedUser);
//                newWallet.setBalance(new BigDecimal("50")); // Initialize wallet with bonus balance of 50
//                newWallet.setCurrency("INR");
                walletRepository.save(newWallet);
                
                return savedUser;
            });
    }

	public void completeUserProfile(CompleteProfileRequest request, Jwt jwt) {
		String auth0Id = jwt.getSubject();
	
		UserEntity user = userRepository.findByAuth0Id(auth0Id)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setDateOfBirth(request.dateOfBirth());
		
		user.setIsProfileComplete(true);
		
		Wallet wallet = walletRepository.findByUser(user)
				.orElseThrow(() -> new WalletNotFoundException("Wallet not found exception"));
		wallet.setIsActive(true);
		walletRepository.save(wallet);
		userRepository.save(user);
	}
}
