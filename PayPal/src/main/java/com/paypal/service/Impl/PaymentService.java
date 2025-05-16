package com.paypal.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.PaymentMode;
import com.paypal.gateway.PaymentGateway;
import com.paypal.repository.NotificationRepository;
import com.paypal.repository.PayeeRepository;
import com.paypal.repository.TransactionRepository;
import com.paypal.repository.UserRepository;
import com.paypal.repository.WalletRepository;

@Service
public class PaymentService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PayeeRepository payeeRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	private final PaymentGateway paymentGateway;

	@Autowired
	public PaymentService(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	public void makePayment(User user, double amount, Payee payee, PaymentMode paymentMode) {
		//User user = userRepository.findById(userId)
		
		paymentGateway.processPayment(user, amount, payee, paymentMode);
	}

}
