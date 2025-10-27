package com.fintech.wallet.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.CompleteProfileRequest;
import com.fintech.wallet.dto.UserResponse;
import com.fintech.wallet.service.UserService;


@RestController
@RequestMapping("/me")
public class ProfileController {

	private final UserService userService;

	public ProfileController(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * Gets the profile of the currently authenticated user.
	 * The user's identity is taken from the validated JWT.
	 */
	
	@GetMapping
	public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
		UserResponse userResponse = userService.findOrCreateUser(jwt);
		return ResponseEntity.ok(userResponse);

	}
	
	@PatchMapping
	public ResponseEntity<Void> completeProfile(@RequestBody CompleteProfileRequest request, @AuthenticationPrincipal Jwt jwt) {
		userService.completeUserProfile(request, jwt);
		return ResponseEntity.noContent().build();
	}
}
