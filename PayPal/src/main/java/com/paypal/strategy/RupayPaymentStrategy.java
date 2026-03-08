package com.paypal.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

@Component
public class RupayPaymentStrategy implements PaymentStrategy{

	private static final Logger logger = LoggerFactory.getLogger(UPIPaymentStrategy.class);
	
	@Override
	public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		logger.info("Processing RUPAY card payment | user={} | payee={} | amount={} | mode={}", user.getName(), payee.getName(), amount, paymentMode);
	}
}
