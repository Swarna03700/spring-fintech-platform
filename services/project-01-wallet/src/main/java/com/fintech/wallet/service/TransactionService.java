package com.fintech.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintech.wallet.dto.TransactionResponse;
import com.fintech.wallet.dto.TransferRequest;
import com.fintech.wallet.entity.Transaction;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.exception.InsufficientBalanceException;
import com.fintech.wallet.exception.InvalidAmountException;
import com.fintech.wallet.exception.TransactionNotFoundException;
import com.fintech.wallet.exception.UserNotFoundException;
import com.fintech.wallet.exception.WalletNotFoundException;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;

@Service
public class TransactionService {

	private UserRepository userRepository;
	private WalletRepository walletRepository;
	private TransactionRepository transactionRepository;
	

	public TransactionService(UserRepository userRepository, WalletRepository walletRepository,
			TransactionRepository transactionRepository) {
		this.userRepository = userRepository;
		this.walletRepository = walletRepository;
		this.transactionRepository = transactionRepository;
	}
	
	@Transactional
	public TransactionResponse performTransfer(String senderAuth0Id, TransferRequest request) {
		UserEntity user = userRepository.findByAuth0Id(senderAuth0Id)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		Wallet senderWallet = walletRepository.findByUser(user)
				.orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

		UserEntity receiver = userRepository.findByEmail(request.receiverEmail())
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		Wallet receiverWallet = walletRepository.findByUser(receiver)
				.orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
		
		// Check if the requested amount is valid or not
		if(request.amount().equals(new BigDecimal(0)) || request.amount().compareTo(new BigDecimal("0")) == -1)
			throw new InvalidAmountException("Invalid amount");
		
		// Check if the sender wallet balance is sufficient to make a transfer
		else if(senderWallet.getBalance().compareTo(request.amount()) < 0)
			throw new InsufficientBalanceException("Insufficient balance");
		
		// Debit the money from sender wallet
		senderWallet.debit(request.amount());

		// Credit the money to the receiver wallet
		receiverWallet.credit(request.amount());

		walletRepository.save(senderWallet);
		walletRepository.save(receiverWallet);

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), request.amount(), "INR", "TRANSFER", senderWallet, receiverWallet);
		transaction.setStatus("SUCCESS");
		Transaction savedTransaction = transactionRepository.saveAndFlush(transaction);

		return new TransactionResponse(savedTransaction.getTransactionId(), savedTransaction.getAmount(), savedTransaction.getStatus(), savedTransaction.getType(),
				user.getEmail(), receiver.getEmail(), savedTransaction.getCreatedAt().toString());
	}
	
//	public TransactionResponse getTransactionById(String transactionId) {
//		
//		return ledgerEntriesRepository.findByTransactionId(transactionId)
//				.orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
//	}

}
