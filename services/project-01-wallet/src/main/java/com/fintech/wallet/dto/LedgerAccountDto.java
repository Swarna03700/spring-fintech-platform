package com.fintech.wallet.dto;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LedgerAccountDto(
		String id,
		String name,
		String description,
		Map<String, Object> metadata,
		
		@JsonProperty("normal_balance")
		String normalBalance,
		
		BigDecimal balance,
		
		@JsonProperty("ledger_id")
		String ledgerId,
		
		@JsonProperty("created_at")
		String createdAt,
		
		@JsonProperty("updated_at")
		String updatedAt
		) {

}
