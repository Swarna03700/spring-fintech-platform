package com.fintech.wallet.entity;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ledger_account_balances")
public class LedgerAccountBalances {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "ledger_account_id")
	private LedgerAccount ledgerAccount;
	
	@Enumerated(EnumType.STRING)
	private BalanceType balanceType;
	
	private BigInteger credits;
	private BigInteger debits;
	private BigInteger amount;
	private String currency;
	
	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	public LedgerAccountBalances() {}
	
	public UUID getId() {
		return id;
	}
	public LedgerAccount getLedgerAccount() {
		return ledgerAccount;
	}
	public BalanceType getBalanceType() {
		return balanceType;
	}
	public BigInteger getCredits() {
		return credits;
	}
	public BigInteger getDebits() {
		return debits;
	}
	public BigInteger getAmount() {
		return amount;
	}
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public void setLedgerAccount(LedgerAccount ledgerAccount) {
		this.ledgerAccount = ledgerAccount;
	}
	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}
	public void setCredits(BigInteger credits) {
		this.credits = credits;
	}
	public void setDebits(BigInteger debits) {
		this.debits = debits;
	}
	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
