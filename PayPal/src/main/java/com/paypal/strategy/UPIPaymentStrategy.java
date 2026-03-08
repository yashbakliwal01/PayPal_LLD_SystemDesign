package com.paypal.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

@Component
public class UPIPaymentStrategy implements PaymentStrategy{

	private static final Logger logger = LoggerFactory.getLogger(UPIPaymentStrategy.class);
	
	@Override
	public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		
		logger.info(
                "Processing UPI payment | user={} | userUpi={} | payee={} | payeeUpi={} | amount={}",
                user.getName(),
                user.getUpiId(),
                payee.getName(),
                payee.getUpiId(),
                amount
        );
		
		boolean paymentSuccess = initiateUPIPayment(user, payee, amount);
		
		if(paymentSuccess) {
			String txnId = generateTransactionId();
			
			logger.info("UPI payment successful | user={} | payee={} | amount={} | txnId={}", user.getName(), payee.getName(), amount, txnId);
		}else {
			logger.error("UPI payment failed | user={} | payee={} | amount={}", user.getName(), payee.getName(), amount);
		}
	}

	private boolean initiateUPIPayment(User user, Payee payee, double amount) {
		logger.info("Initiating UPI payment request | from={} | to={} | amount={}", user.getUpiId(), payee.getUpiId(), amount);
		return Math.random() > 0.2; // Simulate 80% success rate
	}
	
	private String generateTransactionId() {
		return "TXN" + System.currentTimeMillis();
		
	}
}
