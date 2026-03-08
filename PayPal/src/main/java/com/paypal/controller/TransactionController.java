package com.paypal.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.dto.TransactionDTO;
import com.paypal.entity.Transaction;
import com.paypal.repository.TransactionRepository;
import com.paypal.service.strategy.MasterCardPaymentStrategy;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private static final Logger logger = LoggerFactory.getLogger(MasterCardPaymentStrategy.class);
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@GetMapping("/history/{userId}")
	public ResponseEntity<?> getTransactionHistory(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		
		logger.info("Fetching transaction history for userId: {}, page: {}, size: {}", userId, page, size);
		
		if(page<0 || size<=0) {
			logger.warn("Invalid pagination request.");
			return ResponseEntity.badRequest().body("Page must be >= 0 and size must be > 0");
		}
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Transaction> transactionPage = transactionRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
		
		if(transactionPage.isEmpty()) {
			logger.warn("No transactions found for userId: {}", userId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No transactions found for user id", "userId", userId));
		}
		
		List<TransactionDTO> transactions = transactionPage.getContent().stream()
				.map(t->new TransactionDTO(
						t.getId(),
						t.getAmount(),
						t.getPaymentMode(),
						t.getTransactionType(),
						t.getTimestamp()
				))
				.toList();
		
		logger.info("Transactions fetched successfully for userId={}", userId);
		
		return ResponseEntity.ok(Map.of(
				"content", transactions,
				"page", transactionPage.getNumber(),
				"size", transactionPage.getSize(),
				"totalElements", transactionPage.getTotalElements(),
				"totalPages", transactionPage.getTotalPages()
		));
	}

}