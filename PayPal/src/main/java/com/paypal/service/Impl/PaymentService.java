package com.paypal.service.Impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypal.entity.Payee;
import com.paypal.entity.Transaction;
import com.paypal.entity.User;
import com.paypal.entity.Wallet;
import com.paypal.enums.PaymentMode;
import com.paypal.exception.FraudulentTransactionException;
import com.paypal.exception.InsufficientBalanceException;
import com.paypal.exception.PaymentException;
import com.paypal.gateway.PaymentGateway;
import com.paypal.repository.PayeeRepository;
import com.paypal.repository.TransactionRepository;
import com.paypal.repository.UserRepository;
import com.paypal.repository.WalletRepository;

@Service
@Transactional
public class PaymentService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PayeeRepository payeeRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private FraudDetectionService fraudDetectionService;
	
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	
	private final PaymentGateway paymentGateway;

	@Autowired
	public PaymentService(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	@SuppressWarnings("null")
	public void makePayment(User user, double amount, Payee payee, PaymentMode paymentMode) {
		Transaction transaction = new Transaction();

		try {
			if(fraudDetectionService.isFraudulentTransaction(user, amount, payee)) {
				throw new FraudulentTransactionException("Fraudulent transaction detected! Payment aborted.");
			}
			
			//check wallet balance
			Wallet wallet = user.getWallet();
			if(wallet.getBalance()<amount) {
				throw new InsufficientBalanceException("Insufficient wallet balance.");
			}
			wallet.setBalance(wallet.getBalance() - amount);
			walletRepository.save(wallet);
			
			try {
				paymentGateway.processPayment(user, amount, payee, paymentMode);
			}catch(Exception e) {
				throw new PaymentException("Payment processing FAILED.", e);
			}
			
			//save transaction
			//Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setTimestamp(LocalDateTime.now());
			transaction.setRefund(false);
			transaction.setUser(user);
			transaction.setPayee(payee);
			transaction.setPaymentMode(paymentMode);
			transactionRepository.save(transaction);
			
			//notify success
			notificationServiceImpl.notifyPayee(payee, user, amount, transaction.getId(), true, null);
		}catch(Exception e) {
			//notify failure
		 notificationServiceImpl.notifyPayee(
				 payee, 
				 user, 
				 amount, 
				 (transaction!=null)?transaction.getId(): null, 
				 false,
				 e.getMessage());
		 throw e;//rethrow to propagate
	 }
	}
}
