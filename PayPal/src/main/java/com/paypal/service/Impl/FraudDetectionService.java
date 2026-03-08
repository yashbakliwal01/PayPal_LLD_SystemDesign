package com.paypal.service.Impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

@Service
public class FraudDetectionService {
	
	private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);
	private final Map<Long, LocalDateTime> lastTransactionTimeMap = new HashMap<>();
	private final Set<String> blockedPayees = new HashSet<>();
	
	public FraudDetectionService() {
        blockedPayees.add("fraud@upi");
        blockedPayees.add("scam@upi");
    }
	
	public boolean isFraudulentTransaction(User user, double amount, Payee payee) {
		
		
		logger.info("Running fraud detection for user={} payee={} amount={}", user.getId(), payee.getName(), amount);
		
		//Rule-1: checks high value transaction
		if(amount > 100000) {
			logger.warn("High-value transaction flagged for user {} amount Rs. {}",user.getId(), amount);
			return true;
		}

		
		//Rule-2: Rapid repeated transaction check
		LocalDateTime now =LocalDateTime.now();
		LocalDateTime lastTxTime = lastTransactionTimeMap.get(user.getId());
		
		if(lastTxTime != null && lastTxTime.plusSeconds(30).isAfter(now)) {
			logger.warn("Rapid transaction detected for user ID: {}", user.getId());
			return true;
		}
		
		//save transaction time for next comparison
		lastTransactionTimeMap.put(user.getId(), now);
		
		//RULE: 3: Blocked Payee
		if(blockedPayees.contains(payee.getUpiId())) {
			logger.warn("Transaction to blocked payee detected: {}", payee.getUpiId());
			return true;
		}
		
		
		
		logger.info("Transaction passed fraud checks");
		return false;
	}
	
}
