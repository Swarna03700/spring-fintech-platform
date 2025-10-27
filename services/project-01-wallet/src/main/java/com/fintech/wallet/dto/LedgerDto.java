package com.fintech.wallet.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LedgerDto(
		String id,
		String name,
		String description,
		Map<String, Object> metadata,
		@JsonProperty("created_at")
		String createdAt,
		@JsonProperty("updated_at")
		String updatedAt
		) {

}
