package com.paypal.service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;

public interface NotificationService {
    void notifyPayee(Payee payee, User sender, double amount, Long transactionId, boolean success, String failureReason);
}
