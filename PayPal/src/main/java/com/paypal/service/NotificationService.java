package com.paypal.service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

public interface NotificationService {
	void notifyPayee(Payee payee, User sender, double amount, String transactionRef, boolean success, String failureReason);
}
