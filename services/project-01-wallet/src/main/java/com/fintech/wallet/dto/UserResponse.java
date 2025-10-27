package com.fintech.wallet.dto;

public record UserResponse(String userId, String email, String firstName, String lastName, boolean isProfileComplete) {}
