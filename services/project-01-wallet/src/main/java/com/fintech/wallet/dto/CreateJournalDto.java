package com.fintech.wallet.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields:
 * 1. Description
 * 2. Ledger ID
 * 3. Entries
 */
public record CreateJournalDto(
		String description,
		
		@JsonProperty("ledger_id")
		String ledgerId,
		List<EntryRequestDto> entries
		) {

}
