package com.fintech.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fintech.wallet.dto.TransferRequest;
import com.fintech.wallet.entity.Transaction;
import com.fintech.wallet.entity.UserEntity;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.exception.InsufficientBalanceException;
import com.fintech.wallet.exception.InvalidAmountException;
import com.fintech.wallet.exception.UserNotFoundException;
import com.fintech.wallet.exception.WalletNotFoundException;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.UserRepository;
import com.fintech.wallet.repository.WalletRepository;
import com.fintech.wallet.service.TransactionService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@ExtendWith(MockitoExtension.class)
public class TestTransactionService {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private WalletRepository walletRepository;
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@InjectMocks
	private TransactionService transactionService;
	
	// Instance variables
	private UserEntity sender;
	private UserEntity receiver;
	private Wallet senderWallet;
	private Wallet receiverWallet;
	private String senderAuth0Id = "auth0|sender-123";
	private String receiverEmail = "receiver@example.com";
	
	@Captor
	private ArgumentCaptor<Wallet> walletCaptor;

	@Captor
	private ArgumentCaptor<Transaction> transactionCaptor;
	
	// set up method
	@BeforeEach
	void setUp() {
		sender = new UserEntity(senderAuth0Id, "sender@example.com", true);
		senderWallet = new Wallet("sdg7yw8dw8", sender, new BigDecimal("50"), "INR", true);
		
		when(userRepository.findByAuth0Id(senderAuth0Id)).thenReturn(Optional.of(sender));
		when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
		
	}
	
	private void setUpReceiverMocks() {
		receiver = new UserEntity("auth0|receiver-456", receiverEmail, true);
		receiverWallet = new Wallet("yt7e38e889", receiver, new BigDecimal("100"), "INR", true);
		
		when(userRepository.findByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
		when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));
	}
	
	@Test
	void testPerformTransfer_whenSenderHasInsufficientBalance_thenThrowException() {
		setUpReceiverMocks();
		TransferRequest request = new TransferRequest(receiverEmail, new BigDecimal("100"));
		
		InsufficientBalanceException thrownException = assertThrows(
				InsufficientBalanceException.class,
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTranfer_whenSenderEntersZeroTransferAmount() {
		setUpReceiverMocks();
		TransferRequest request = new TransferRequest("receiver@example.com", new BigDecimal("0"));
		
		InvalidAmountException thrownException = assertThrows(InvalidAmountException.class, 
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTransfer_whenSenderEntersNegativeTransferAmount() {
	    setUpReceiverMocks();
		TransferRequest request = new TransferRequest("receiver@example.com", new BigDecimal("-1"));
		
		InvalidAmountException thrownException = assertThrows(InvalidAmountException.class, 
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTransfer_whenReceiverNotFound() {
		String nonExistentEmail = "non.existent@example.com";
		TransferRequest request = new TransferRequest(nonExistentEmail, new BigDecimal("20"));
		
		when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());
		
		UserNotFoundException thrownException = assertThrows(UserNotFoundException.class,
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTransfer_whenSenderWalletNotFound_thenThrowException() {
		TransferRequest request = new TransferRequest(receiverEmail, new BigDecimal("20"));
		
		when(walletRepository.findByUser(sender)).thenReturn(Optional.empty());
		
		WalletNotFoundException thrownException = assertThrows(WalletNotFoundException.class, 
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTransfer_whenReceiverWalletNotFound_thenThrowException() {
		TransferRequest request = new TransferRequest(receiverEmail, new BigDecimal("20"));
		
		setUpReceiverMocks();
		when(walletRepository.findByUser(receiver)).thenReturn(Optional.empty());
		
		WalletNotFoundException thrownException = assertThrows(WalletNotFoundException.class, 
				() -> transactionService.performTransfer(senderAuth0Id, request)
				);
		
		verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
	}
	
	@Test
	void testPerformTransfer_whenSenderHasExactBalance_shouldSucceed() {

		setUpReceiverMocks();
		when(transactionRepository.saveAndFlush(any(Transaction.class)))
	    .thenAnswer(invocation -> {
	        Transaction t = invocation.getArgument(0);
	        // emulate DB-generated value
	        t.setCreatedAt(Instant.parse("2025-10-15T12:00:00Z")); // or Instant.now()
	        return t;
	    });
		TransferRequest request = new TransferRequest(receiverEmail, new BigDecimal("50"));
		
		transactionService.performTransfer(senderAuth0Id, request);
		
		verify(walletRepository, times(2)).save(walletCaptor.capture());
		verify(transactionRepository, times(1)).saveAndFlush(transactionCaptor.capture());
		
		Wallet savedSenderWallet = walletCaptor.getAllValues().get(0);
		Wallet savedReceiverWallet = walletCaptor.getAllValues().get(1);
		
		assertEquals(0, BigDecimal.ZERO.compareTo(savedSenderWallet.getBalance()), "Sender's balance should be exactly zero");
		assertEquals(0, new BigDecimal("150").compareTo(savedReceiverWallet.getBalance()), "Receiver's balance should be 150");
	}
	
	@Test
	void testPerformTransfer_successfulTransfer() {
		setUpReceiverMocks();
		when(transactionRepository.saveAndFlush(any(Transaction.class)))
	    .thenAnswer(invocation -> {
	        Transaction t = invocation.getArgument(0);
	        // emulate DB-generated value
	        t.setCreatedAt(Instant.parse("2025-10-15T12:00:00Z")); // or Instant.now()
	        return t;
	    });
		TransferRequest request = new TransferRequest(receiverEmail, new BigDecimal("40"));
		
		transactionService.performTransfer(senderAuth0Id, request);
		
		verify(walletRepository, times(2)).save(walletCaptor.capture());
		verify(transactionRepository, times(1)).saveAndFlush(transactionCaptor.capture());
		

		Wallet savedSenderWallet = walletCaptor.getAllValues().get(0);
		Wallet savedReceiverWallet = walletCaptor.getAllValues().get(1);
		
		assertEquals(0, new BigDecimal("10").compareTo(savedSenderWallet.getBalance()), "Sender's balance should be 10");
		assertEquals(0, new BigDecimal("140").compareTo(savedReceiverWallet.getBalance()), "Receiver's balance should be 140");
	}
}
