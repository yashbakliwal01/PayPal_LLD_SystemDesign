package com.paypal.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypal.entity.Payee;
import com.paypal.entity.Transaction;
import com.paypal.entity.User;
import com.paypal.entity.Wallet;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;
import com.paypal.enums.TransactionStatus;
import com.paypal.enums.TransactionType;
import com.paypal.exception.AlreadyRefundedException;
import com.paypal.exception.FraudulentTransactionException;
import com.paypal.exception.InsufficientBalanceException;
import com.paypal.exception.PaymentException;
import com.paypal.exception.TransactionNotFoundException;
import com.paypal.gateway.PaymentGateway;
import com.paypal.repository.TransactionRepository;
import com.paypal.repository.WalletRepository;
import com.paypal.util.TransactionIdGenerator;

@Service
@Transactional
public class PaymentService {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private FraudDetectionService fraudDetectionService;
	
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	
	@Autowired
	private PaymentGateway paymentGateway;
	
	public PaymentService(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	
	public void makePayment(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		try {
			
			logger.info("Payment initiated from user = {} | payee = {} | amount = {}", user.getName(), payee.getName(), amount);
			
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
			logger.info("Wallet balance before deduction: {}", wallet.getBalance());
			
			wallet.setBalance(wallet.getBalance() - amount);
			
			logger.info("Wallet balance after deduction: {}", wallet.getBalance());
			
			walletRepository.save(wallet);
			
			//4. Process Payment via Gateway
			paymentGateway.processPayment(user, amount, payee, paymentMode, cardType);
			
			//5. Save Transaction
			Transaction transaction = new Transaction();
			transaction.setTransactionRef(TransactionIdGenerator.generateTransactionRef());
			transaction.setAmount(amount);
			transaction.setUser(user);
			transaction.setPayee(payee);
			transaction.setPaymentMode(paymentMode);
			transaction.setTransactionType(TransactionType.PAYMENT);
			transaction.setStatus(TransactionStatus.SUCCESS);
			
			transactionRepository.save(transaction);
			
			logger.info("Transaction saved with id {}", transaction.getId());
			
			
			//6. Notify Success
			notificationServiceImpl.notifyPayee(payee, user, amount, transaction.getTransactionRef(), true, null);
			
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
	public boolean refundTransaction(String transactionRef, boolean confirmIfAlreadyRefunded) {
		
		logger.info("Refund request received for transactionId={}", transactionRef);
		Transaction originalTransaction = transactionRepository.findByTransactionRef(transactionRef)
				.orElseThrow(() -> {
					logger.error("Transaction not found for transactionRef={}", transactionRef);
				    return new TransactionNotFoundException(
				            "Transaction not found for reference: " + transactionRef
				    );
				});
		//check if already refunded
		boolean alreadyRefunded = transactionRepository.existsByTransactionRefAndTransactionType(transactionRef, TransactionType.REFUND);
		if(alreadyRefunded && !confirmIfAlreadyRefunded) {
			logger.warn("Refund already processed for transactionRef={}", originalTransaction.getTransactionRef());
			throw new AlreadyRefundedException("Transaction already refunded");
		}
		
		User user = originalTransaction.getUser();
		Wallet wallet = user.getWallet();
		
		double refundAmount = originalTransaction.getAmount();
		
		//refund wallet balance
		logger.info(">>>> Processing REFUND | transactionRef={} | amount={}", originalTransaction.getTransactionRef(), refundAmount);
		wallet.setBalance(wallet.getBalance() + refundAmount);
		logger.info(">>>********** Wallet credited**********| newBalance={}", wallet.getBalance());
		walletRepository.save(wallet);
		
		
		//create refund transaction ref:
		String refundRef = TransactionIdGenerator.generateTransactionRef();
		
		//check refund transaction
		Transaction refundTransaction = new Transaction();
		refundTransaction.setTransactionRef(refundRef);
		refundTransaction.setAmount(refundAmount);
		refundTransaction.setUser(user);
		refundTransaction.setPayee(originalTransaction.getPayee());
		refundTransaction.setPaymentMode(originalTransaction.getPaymentMode());
		refundTransaction.setTransactionType(TransactionType.REFUND);
		refundTransaction.setStatus(TransactionStatus.SUCCESS);
		
		transactionRepository.save(refundTransaction);
		
		logger.info("Refund transaction created | refundRef={}", refundRef);
		
		//notify success
		notificationServiceImpl.notifyPayee(
				originalTransaction.getPayee(), 
				user, 
				refundAmount, 
				refundTransaction.getTransactionRef(), 
				true, 
				"Refund processed Successfully.");
		
		
		return true;	
		
	}
}
