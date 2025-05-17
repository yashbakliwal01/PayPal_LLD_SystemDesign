package com.paypal.service.strategy;

import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

@Component
public class RupayPaymentStrategy implements PaymentStrategy{

	@Override
	public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cvardType) {
		// TODO Auto-generated method stub
		System.out.println("Inside rupay");
	}


}
