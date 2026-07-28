package com.fintech.wallet.dto;

import java.util.Map;

import jakarta.validation.constraints.Size;

public record CreateLedgerRequest(
		String name,
		
		@Size(max = 255, message = "Description must be 255 characters or less")
		String description,
		Map<String, Object> metadata
		) {

}
