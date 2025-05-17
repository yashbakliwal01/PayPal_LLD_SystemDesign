package com.paypal.service.strategy;

import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

@Component
public class VisaPaymentStrategy implements PaymentStrategy{

	@Override
	public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		System.out.println("Inside the visa");
	}

}
