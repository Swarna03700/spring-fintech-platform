package com.fintech.wallet.dto;

import java.math.BigDecimal;

public record TransferRequest(String receiverEmail, BigDecimal amount) {

}
