package com.fintech.wallet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.wallet.entity.Entry;

public interface EntryRepository extends JpaRepository<Entry, UUID> {
	
	List<Entry> findByJournalId(UUID journalId);
}
