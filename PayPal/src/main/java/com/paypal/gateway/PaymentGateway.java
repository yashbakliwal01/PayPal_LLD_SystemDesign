package com.paypal.gateway;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

public interface PaymentGateway {
	void processPayment(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType);
}
