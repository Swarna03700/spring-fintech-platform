package com.fintech.wallet.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JournalDto(
		String id,
		String description,
		String status,
		
		@JsonProperty("ledger_id")
		String ledgerId,
		List<EntryDto> entries,
		
		@JsonProperty("created_at")
		String createdAt,
		
		@JsonProperty("updated_at")
		String updatedAt
		) {

}
