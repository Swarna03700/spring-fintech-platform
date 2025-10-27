package com.fintech.wallet.exception;

public class LedgerNotFoundException extends RuntimeException {
	
	public LedgerNotFoundException(String message) {
		super(message);
	}
}
