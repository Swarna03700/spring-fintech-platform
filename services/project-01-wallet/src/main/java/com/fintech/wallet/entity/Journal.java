package com.fintech.wallet.entity;

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
@Table(name = "journals")
public class Journal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String description;
	
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "ledger_id")
	private Ledger ledger;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	public Journal() {}
	
	public UUID getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public String getStatus() {
		return status;
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
	public void setDescription(String description) {
		this.description = description;
	}
	public void setStatus(String status) {
		this.status = status;
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
	
	
}
