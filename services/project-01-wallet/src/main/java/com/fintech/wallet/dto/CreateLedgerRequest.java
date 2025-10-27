package com.fintech.wallet.dto;

import java.util.Map;

public record CreateLedgerRequest(
		String name,
		String description,
		Map<String, Object> metadata
		) {

}
