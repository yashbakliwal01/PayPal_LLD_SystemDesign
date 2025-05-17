package com.paypal.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

@Service
public class FraudDetectionService {
	
	private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);
//	private final Map<Long, LocalDateTime> lastTransactionTimeMap = new HashMap<>();
//	private final Set<String> blockedPayees = new HashSet<>();
//	private final Map<Long, String> userRegions = new HashMap<>();
//	
//	public FraudDetectionService() {
//		blockedPayees.add("fraud@upi");
//		blockedPayees.add("suspicious@upi");
//		
//		userRegions.put(1L, "IN");
//		userRegions.put(2L, "US");
//	}
	
	public boolean isFraudulentTransaction(User user, double amount, Payee payee) {
		//Rule-1: checks high value transaction
		if(amount > 100000) {
			logger.warn("High-value transaction flagged: Rs. {}", amount);
			return true;
		}

		
//		//Rule-2: Rapid repeated transaction check
//		LocalDateTime now =LocalDateTime.now();
//		LocalDateTime lastTxTime = lastTransactionTimeMap.get(user.getId());
//		
//		if(lastTxTime != null && lastTxTime.plusSeconds(30).isAfter(now)) {
//			logger.warn("Rapid transaction detected for user ID: {}", user.getId());
//			return true;
//		}
//		
//		//save transaction time for next comparison
//		lastTransactionTimeMap.put(user.getId(), now);
		return false;
	}
//	
//	private String getPayeeRegion(Payee payee) {
//		
//		if(payee.getUpiId().endsWith("@upi")) {
//			return "IN";
//		}else if(payee.getUpiId().endsWith("@paypal")) {
//			return "US";
//		}
//		
//		return "UNKNOWN";
	
}
