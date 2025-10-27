package com.fintech.wallet.exception;

public class MaximumTransactionLimitException extends RuntimeException {
	
	public MaximumTransactionLimitException(String message) {
		super(message);
	}
}
