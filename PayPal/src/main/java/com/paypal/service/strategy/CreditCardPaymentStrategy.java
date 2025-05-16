package com.paypal.service.strategy;

import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy{

	@Override
	public void pay(User user, double amount, Payee payee) {}

}
