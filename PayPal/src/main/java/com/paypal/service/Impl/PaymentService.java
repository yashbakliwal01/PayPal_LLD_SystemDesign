package com.paypal.service.Impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypal.entity.Payee;
import com.paypal.entity.Transaction;
import com.paypal.entity.User;
import com.paypal.entity.Wallet;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;
import com.paypal.exception.AlreadyRefundedException;
import com.paypal.exception.FraudulentTransactionException;
import com.paypal.exception.InsufficientBalanceException;
import com.paypal.exception.PaymentException;
import com.paypal.exception.TransactionNotFoundException;
import com.paypal.gateway.PaymentGateway;
import com.paypal.repository.TransactionRepository;
import com.paypal.repository.WalletRepository;
import com.paypal.service.strategy.MasterCardPaymentStrategy;
import com.paypal.service.strategy.PaymentStrategy;
import com.paypal.service.strategy.RupayPaymentStrategy;
import com.paypal.service.strategy.VisaPaymentStrategy;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class PaymentService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private FraudDetectionService fraudDetectionService;
	
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	
	private final PaymentGateway paymentGateway;

	private Map<CardType, PaymentStrategy> cardStrategyMap;
	
	public PaymentService(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	
	@PostConstruct
	private void initCardStrategies() {
		cardStrategyMap = Map.of(
				CardType.VISA, new VisaPaymentStrategy(),
				CardType.RUPAY, new RupayPaymentStrategy(),
				CardType.MASTERCARD, new MasterCardPaymentStrategy()
				);
	}
	
	
	public void makePayment(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		try {
			//1. Transaction Fraud Detection
			if(fraudDetectionService.isFraudulentTransaction(user, amount, payee)) {
				throw new FraudulentTransactionException("Fraudulent transaction detected! Payment aborted.");
			}
			
			//2. Wallet Balance check
			Wallet wallet = user.getWallet();
			if(wallet.getBalance() < amount) {
				throw new InsufficientBalanceException("Insufficient wallet balance.");
			}
			
			//3. Deduct Balance
			wallet.setBalance(wallet.getBalance() - amount);
			walletRepository.save(wallet);
			
			//4. Process Payment (via using Strategy Pattern + Gateway)
			if(paymentMode == PaymentMode.CREDIT_CARD || paymentMode == PaymentMode.DEBIT_CARD) {
		        PaymentStrategy strategy = cardStrategyMap.get(cardType);
		        if (strategy == null) throw new IllegalArgumentException("Unsupported card type");
		        strategy.pay(user, amount, payee, paymentMode, cardType);
		    }
			paymentGateway.processPayment(user, amount, payee, paymentMode, cardType);
			
			//5. Save Transaction
			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setTimestamp(LocalDateTime.now());
			transaction.setRefund(false);
			transaction.setUser(user);
			transaction.setPayee(payee);
			transaction.setPaymentMode(paymentMode);
			transactionRepository.save(transaction);
			
			//6. Notify Success
			notificationServiceImpl.notifyPayee(payee, user, amount, transaction.getId(), true, null);
		}catch(Exception e) {
			//7. Notify Failure
			notificationServiceImpl.notifyPayee(
					 payee, 
					 user, 
					 amount, 
					 null, 
					 false,
					 e.getMessage());
		 throw new PaymentException("Payment processing FAILED.", e);
		}
	}
	
	//Refund Transaction
	public boolean refundTransaction(Long transactionId, boolean confirmIfAlreadyRefunded) {
		Transaction originalTransaction = transactionRepository.findById(transactionId)
				.orElseThrow(() -> new TransactionNotFoundException("Original Transaction not found with ID: " + transactionId));
		
		if(originalTransaction.isRefund() && !confirmIfAlreadyRefunded) {
			throw new AlreadyRefundedException("Transaction already refunded.");
		}
		
		originalTransaction.setRefund(true);
		transactionRepository.save(originalTransaction);
		
		User user = originalTransaction.getUser();
		Wallet wallet = user.getWallet();
		double refundAmount = originalTransaction.getAmount();
		
		wallet.setBalance(wallet.getBalance() + refundAmount);
		walletRepository.save(wallet);
		
		Transaction refundTransaction = new Transaction();
		refundTransaction.setAmount(refundAmount);
		refundTransaction.setTimestamp(LocalDateTime.now());
		refundTransaction.setRefund(true);
		refundTransaction.setUser(user);
		refundTransaction.setPayee(originalTransaction.getPayee());
		refundTransaction.setPaymentMode(originalTransaction.getPaymentMode());
		transactionRepository.save(refundTransaction);
		
		notificationServiceImpl.notifyPayee(
				originalTransaction.getPayee(), 
				user, 
				refundAmount, 
				refundTransaction.getId(), 
				true, 
				"Refund processed Successfully.");
		return confirmIfAlreadyRefunded;
	}
}
