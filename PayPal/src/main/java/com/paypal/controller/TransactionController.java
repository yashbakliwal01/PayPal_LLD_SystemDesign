package com.paypal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.Transaction;
import com.paypal.entity.TransactionDTO;
import com.paypal.repository.TransactionRepository;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@GetMapping("/history/{userId}")
	public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size){
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Transaction> transactionPage = transactionRepository.findByUserId(userId, pageable);
		
		if(transactionPage.getContent().isEmpty()) {
			return ResponseEntity.status(404).body("No transactions found for user id: " + userId);
        }
		
		List<TransactionDTO> transactionDTO = transactionPage.stream()
				.map(t->new TransactionDTO(t.getId(), t.getAmount(), t.getPaymentMode(), t.getTimestamp())).toList();
		
		return ResponseEntity.ok(transactionDTO);	}
}
