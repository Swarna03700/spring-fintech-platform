package com.fintech.wallet.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateLedgerAccount(
		String name,
		String description,
		Map<String, Object> metadata,
		
		@JsonProperty("normal_balance")
		String normalBalance,
		
		@JsonProperty("ledger_id")
		String ledgerId
		) {

}
