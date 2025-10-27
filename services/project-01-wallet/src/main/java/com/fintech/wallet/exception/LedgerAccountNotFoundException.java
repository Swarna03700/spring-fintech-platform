package com.fintech.wallet.exception;

public class LedgerAccountNotFoundException extends RuntimeException {
	
	public LedgerAccountNotFoundException(String message) {
		super(message);
	}
}
