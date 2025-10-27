package com.fintech.wallet.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fintech.wallet.dto.ErrorResponse;
import com.fintech.wallet.exception.DuplicateUserException;
import com.fintech.wallet.exception.InsufficientBalanceException;
import com.fintech.wallet.exception.InvalidAmountException;
import com.fintech.wallet.exception.LedgerAccountNotFoundException;
import com.fintech.wallet.exception.LedgerNotFoundException;
import com.fintech.wallet.exception.MaximumTransactionLimitException;
import com.fintech.wallet.exception.TransactionNotFoundException;
import com.fintech.wallet.exception.UserNotFoundException;
import com.fintech.wallet.exception.WalletNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream().map(err -> err.getField() + " "+err.getDefaultMessage()).findFirst()
.orElse("Invalid input");
		ErrorResponse error = new ErrorResponse(
				"BAD_REQUEST",
				message,
				Instant.now()
				);
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
		ErrorResponse error = new ErrorResponse(
				"USER_ALREADY_EXISTS",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(
				"WALLET_NOT_FOUND",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
		ErrorResponse error = new ErrorResponse(
				"BAD_REQUEST",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler()
	public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
		ErrorResponse error = new ErrorResponse(
				"BAD_REQUEST",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(
				"NOT_FOUND",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMaximumTransactionLimit(MaximumTransactionLimitException ex) {
		ErrorResponse error = new ErrorResponse(
				"BAD_REQUEST",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
		ErrorResponse error = new ErrorResponse(
				"UNAUTHORIZED",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(
				"NOT_FOUND",
				ex.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleLedgerNotFoundException(LedgerNotFoundException e) {
		ErrorResponse error = new ErrorResponse(
				"NOT_FOUND",
				e.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleLedgerAccountNotFoundException(LedgerAccountNotFoundException e) {
		ErrorResponse error = new ErrorResponse(
				"NOT_FOUND",
				e.getMessage(),
				Instant.now()
				);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
