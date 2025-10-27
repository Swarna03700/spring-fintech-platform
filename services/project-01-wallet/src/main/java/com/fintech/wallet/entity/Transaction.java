package com.fintech.wallet.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
	
	@Id
	@Column(name = "transaction_id", nullable = false)
	private String transactionId;
	
	@Column(nullable = false)
	private String status = "Pending";
	
	@Column(nullable = false)
	private BigDecimal amount;
	
	private String currency;
	
	@Column(nullable = false)
	private String type;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	@ManyToOne
	@JoinColumn(name = "sender_wallet_id", nullable = false)
	private Wallet sender;
	
	@ManyToOne
	@JoinColumn(name = "receiver_wallet_id", nullable = false)
	private Wallet receiver;
	
	public Transaction() {}
	
	public Transaction(String transactionId, BigDecimal amount, String currency, String type, Wallet sender, Wallet receiver) {
		this.transactionId = transactionId;
		this.amount = amount;
		this.currency = currency;
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public String getTransactionId() {
		return this.transactionId;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public BigDecimal getAmount() {
		return this.amount;
	}
	
	public String getCurrency() {
		return this.currency;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Wallet getSender() {
		return this.sender;
	}
	
	public Wallet getReceiver() {
		return this.receiver;
	}
	
	public Instant getCreatedAt() {
		return this.createdAt;
	}
	// only for mutable fields
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
