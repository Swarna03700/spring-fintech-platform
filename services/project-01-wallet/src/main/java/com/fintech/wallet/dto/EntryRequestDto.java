package com.fintech.wallet.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fields:
 * 1. direction
 * 2. amount
 * 3. ledgerAccountId
 * 4. journalId
 */
public record EntryRequestDto(
		String direction,
		BigDecimal amount,
		
		@JsonProperty("ledger_account_id")
		String ledgerAcoountId
		) {

}
