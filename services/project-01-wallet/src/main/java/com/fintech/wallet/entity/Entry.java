package com.fintech.wallet.entity;

import java.math.BigInteger;
import java.time.Instant;
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

@Entity
@Table(name = "entries")
public class Entry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "ledger_account_id", nullable = false)
	private LedgerAccount ledgerAccountId;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Journal journal;
	
	@Column(nullable = false)
	private String direction;
	
	@Column(nullable = false)
	private BigInteger amount;
	
	private String status;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	public Entry() {}
	
	public UUID getId() {
		return id;
	}
	
	public LedgerAccount getLedgerAccountId() {
		return ledgerAccountId;
	}
	
	public Journal getJournal() {
		return this.journal;
	}
	
	public String getDirection() {
		return direction;
	}
	public BigInteger getAmount() {
		return amount;
	}
	public String getStatus() {
		return status;
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
	
	public void setJournal(Journal journal) {
		this.journal = journal;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public void setLedgerAccountId(LedgerAccount ledgerAccountId) {
		this.ledgerAccountId = ledgerAccountId;
	}
	
	
}
