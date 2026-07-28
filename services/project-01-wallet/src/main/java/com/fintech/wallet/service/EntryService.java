package com.fintech.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.entity.Entry;
import com.fintech.wallet.repository.EntryRepository;
import com.fintech.wallet.utilities.Utils;

@Service
public class EntryService {
	private EntryRepository entryRepository;
	
	public EntryService(EntryRepository entryRepository) {
		this.entryRepository = entryRepository;
	}
	public EntryDto getEntry(String id) {
		Entry entry = entryRepository.findById(UUID.fromString(id))
				.orElseThrow();
		return new EntryDto(
				entry.getId().toString(),
				Utils.convertToFloatingPoint(entry.getAmount()),
				entry.getStatus(),
				entry.getDirection(),
				entry.getLedgerAccountId().getId().toString(),
				entry.getJournal().getId().toString(),
				entry.getCreatedAt().toString(),
				entry.getUpdatedAt().toString()
				);
	}
	
	public List<EntryDto> getAllEntries() {
		List<Entry> entries = entryRepository.findAll();
		List<EntryDto> entryDtos = new ArrayList<>();
		for(Entry entry : entries) {
			EntryDto entryDto = new EntryDto(
									entry.getId().toString(),
									Utils.convertToFloatingPoint(entry.getAmount()),
									entry.getStatus(),
									entry.getDirection(),
									entry.getLedgerAccountId().getId().toString(),
									entry.getJournal().getId().toString(),
									entry.getCreatedAt().toString(),
									entry.getUpdatedAt().toString()
									);
			entryDtos.add(entryDto);
		}
		return entryDtos;
	}
	
}
