package com.fintech.wallet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.EntryDto;
import com.fintech.wallet.service.EntryService;

@RestController
@RequestMapping("/api")
public class EntryController {
	
	private EntryService entryService;
	
	public EntryController(EntryService entryService) {
		this.entryService = entryService;
	}
	
	@GetMapping("/ledger_entries/{id}")
	public ResponseEntity<EntryDto> getEntryById(@PathVariable("id") String id) {
		EntryDto response = entryService.getEntry(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/ledger_entries")
	public ResponseEntity<List<EntryDto>> getAllEntries() {
		List<EntryDto> response = entryService.getAllEntries();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
