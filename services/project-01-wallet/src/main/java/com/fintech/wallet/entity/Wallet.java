package com.fintech.wallet.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallets")
public class Wallet {
	
	@Id
	@Column(name = "wallet_id")
	private String walletId;
	
	@OneToOne
	@JoinColumn(name = "user_auth0_id", nullable = false, unique = true)
	private UserEntity user;
	
	@Column(nullable = false)
	private BigDecimal balance;
	
	@Column(nullable = false, length = 3)
	private String currency;
	
	@Column(name = "is_active", nullable = false)
	private boolean isActive;
	
	@CreationTimestamp
	@Column(name = "created_at")
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	public Wallet() {}
	
	public Wallet(String walletId, UserEntity user, BigDecimal balance, String currency, boolean isActive) {
		this.walletId = walletId;
		this.user = user;
		this.balance = balance;
		this.currency = currency;
		this.isActive = isActive;
	}
	
	public String getWalletId() {
		return this.walletId;
	}
	
	public UserEntity getUser() {
		return this.user;
	}
	
	public BigDecimal getBalance() {
		return this.balance;
	}
	
	public String getCurrency() {
		return this.currency;
	}

	public boolean getIsActive() {
		return this.isActive;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * This method performs debit on a wallet account
	 * @param amount - amount to be debited
	 */
	public BigDecimal debit(BigDecimal amount) {
		this.balance = this.balance.subtract(amount);
		return this.balance;
	}
	
	/**
	 * This method performs credit on a wallet account
	 * @param amount - amount to be credited
	 */
	public BigDecimal credit(BigDecimal amount) {
		this.balance = this.balance.add(amount);
		return this.balance;
	}
}
