package com.paypal.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	 @Override
	 public void notifyPayee(Payee payee, User sender, double amount, Long transactionId, boolean success, String failureReason) {
		String message;
		if(success) {
			message = String.format("Hi %s, you have received %.2f from %s. Transaction ID: %d", payee.getName(), amount, sender.getName(), transactionId);
		}else {
			message = String.format("Hi %s, a transaction of Rs.%.2f from %s failed. Reason: %s", payee.getName(), amount, sender.getName(), failureReason);
		}
		
		sendEmail(payee.getEmail(), message);
		sendSms(payee.getPhoneNumber(), message);
	}
	
	
	private void sendSms(String phoneNumber, String message) {
		  logger.info("Sending SMS to {}: {}", phoneNumber, message);
	}

	private void sendEmail(String email, String message) {
		logger.info("Sending EMAIL to {} : {} ", email, message);
	}

}
