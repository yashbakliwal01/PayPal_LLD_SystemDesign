package com.paypal.service.strategy;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

public interface PaymentStrategy {
	//How to Pay then use STRATEGY pattern
	void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cvardType);
}
