package com.fintech.wallet.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EntryDto(
		String id,
		BigDecimal amount,
		String status,
		String direction,
		
		@JsonProperty("ledger_account_id")
		String ledgerAccountId,
		
		@JsonProperty("journal_id")
		String journalId,
		
		@JsonProperty("created_at")
		String createdAt,
		
		@JsonProperty("updated_at")
		String updatedAt
		) {

}
