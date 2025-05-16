package com.paypal.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

@Component
public class UPIPaymentStrategy implements PaymentStrategy{

	private static final Logger logger = LoggerFactory.getLogger(UPIPaymentStrategy.class);
	
	@Override
	public void pay(User user, double amount, Payee payee) {
		 logger.info("Processing UPI payment of Rs. " + amount + " from user " + user.getName() + " to payee " + payee.getName());
		 
		 boolean paymentSuccess = initiateUPIPayment(user, payee, amount);
		 
		 if(paymentSuccess) {
			 logger.info("UPI payment successful! Transaction ID: " + generateTransactionId());
		 }else {
			 logger.error("UPI payment failed. Please try again.");
		 }
	}

	private boolean initiateUPIPayment(User user, Payee payee, double amount) {
		System.out.println("Initiating UPI payment request...");
        System.out.println("User UPI ID: " + user.getUpiId() + ", Payee UPI ID: " + payee.getUpiId());
        return Math.random() > 0.2;
	}
	
	private String generateTransactionId() {
		 return "TXN" + System.currentTimeMillis(); 
	}

}
