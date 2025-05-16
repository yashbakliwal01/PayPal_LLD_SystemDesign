package com.paypal.service.strategy;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

public interface PaymentStrategy {
	//How to Pay then STRATEGY
	void pay(User user, double amount, Payee payee);
}
