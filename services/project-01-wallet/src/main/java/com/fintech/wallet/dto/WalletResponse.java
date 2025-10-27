package com.fintech.wallet.dto;

import java.math.BigDecimal;


public record WalletResponse(String walletId, BigDecimal balance, String currency, boolean isActive) {
	
}
