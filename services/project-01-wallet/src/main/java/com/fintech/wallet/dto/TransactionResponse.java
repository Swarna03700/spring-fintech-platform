package com.fintech.wallet.dto;

import java.math.BigDecimal;

public record TransactionResponse(String transactionId, BigDecimal amount, String status, String type, String senderInfo,
		String receiverInfo, String timestamp) {

}
