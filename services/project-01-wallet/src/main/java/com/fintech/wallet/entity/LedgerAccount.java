package com.fintech.wallet.entity;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "ledger_accounts")
public class LedgerAccount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String name;
	private String description;
	private Map<String, Object> metadata;
	
	@Column(name = "normal_balance", nullable = false)
	private String normalBalance;
	
	@Column(nullable = false)
	private BigInteger balance;
	
	@ManyToOne
	@JoinColumn(name = "ledger_id")
	private Ledger ledger;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	public LedgerAccount() {}
	
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	public String getNormalBalance() {
		return normalBalance;
	}
	
	public Ledger getLedger() {
		return ledger;
	}
	
	public Instant getCreatedAt() {
		return createdAt;
	}
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
	public void setNormalBalance(String normalBalance) {
		this.normalBalance = normalBalance;
	}
	
	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}
	
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}
}
