package com.fintech.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.wallet.dto.CreateJournalDto;
import com.fintech.wallet.dto.JournalDto;
import com.fintech.wallet.service.JournalService;

@RestController
@RequestMapping("/api")
public class JournalController {
	
	private JournalService journalService;
	
	public JournalController(JournalService journalService) {
		this.journalService = journalService;
	}
	
	@PostMapping("/journals")
	public ResponseEntity<JournalDto> createJournal(@RequestBody CreateJournalDto request) {
		JournalDto response = journalService.createJournal(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/journals/{id}")
	public ResponseEntity<JournalDto> getJournalById(@PathVariable("id") String id) {
		
		JournalDto response = journalService.getJournalById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
