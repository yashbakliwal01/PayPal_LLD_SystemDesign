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
	 public void notifyPayee(Payee payee, User sender, double amount, String transactionRef, boolean success, String failureReason) {
		String message;
		if(success) {
			message = String.format("Hi %s, you have received Rs.%.2f from %s. Transaction Ref: %s", payee.getName(), amount, sender.getName(), transactionRef);
		}else {
			message = String.format("Hi %s, a transaction of Rs.%.2f from %s failed. Reason: %s", payee.getName(), amount, sender.getName(), failureReason);
		}
		
		logger.info("Sending notification to payee {}", payee.getName());
		
		sendEmail(payee.getEmail(), message);
		sendSms(payee.getPhoneNumber(), message);
	}
	
	
	private void sendSms(String phoneNumber, String message) {
		  logger.info("Sending SMS to {}: | message= {}", phoneNumber, message);
	}

	private void sendEmail(String email, String message) {
		logger.info("Sending EMAIL to {} : | message= {} ", email, message);
	}

}
