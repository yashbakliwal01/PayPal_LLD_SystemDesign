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
import com.paypal.repository.PayeeRepository;
import com.paypal.repository.TransactionRepository;
import com.paypal.repository.UserRepository;
import com.paypal.repository.WalletRepository;
import com.paypal.service.strategy.MasterCardPaymentStrategy;
import com.paypal.service.strategy.PaymentStrategy;
import com.paypal.service.strategy.RupayPaymentStrategy;
import com.paypal.service.strategy.VisaPaymentStrategy;

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
	
	//strategy pattern is used here for cardtype
	public void makePayment(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		Map<CardType, PaymentStrategy> cardStrategyMap = Map.of(
				CardType.VISA, new VisaPaymentStrategy(),
				CardType.RUPAY, new RupayPaymentStrategy(),
				CardType.MASTERCARD, new MasterCardPaymentStrategy()
				);
		if (paymentMode == PaymentMode.CREDIT_CARD) {
	        PaymentStrategy strategy = cardStrategyMap.get(cardType);
	        if (strategy == null) throw new IllegalArgumentException("Unsupported card type");
	        strategy.pay(user, amount, payee, paymentMode, cardType);
	    }
		
		
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
				paymentGateway.processPayment(user, amount, payee, paymentMode, cardType);
			}catch(Exception e) {
				throw new PaymentException("Payment processing FAILED.", e);
			}
			
			//save transaction
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
	
	
	//REFUND
	public void refundTransaction(Long transactionId, boolean confirmIfAlreadyRefunded) {
		Transaction originalTransaction = transactionRepository.findById(transactionId).orElseThrow(()->new TransactionNotFoundException("Original Transaction not found with ID: "+transactionId));
		
		//check if refund is already exists for this transaction
		boolean isAlreadyRefunded = transactionRepository.existsById(transactionId);
		
		if(originalTransaction.isRefund() && !confirmIfAlreadyRefunded) {
			throw new AlreadyRefundedException("Transaction already refunded.");
		}
		
		originalTransaction.setRefund(true);
		transactionRepository.save(originalTransaction);

		User user = originalTransaction.getUser();
		Wallet wallet = user.getWallet();
		double refundAmount = originalTransaction.getAmount();
		
		//refund must be credit back to user wallet
		wallet.setBalance(wallet.getBalance()+refundAmount);
		walletRepository.save(wallet);
		
		//creating refund transaction:
		Transaction refundTransaction = new Transaction();
		refundTransaction.setAmount(refundAmount);
		refundTransaction.setTimestamp(LocalDateTime.now());
		refundTransaction.setRefund(true);
		refundTransaction.setUser(user);
		refundTransaction.setPayee(originalTransaction.getPayee());
		refundTransaction.setPaymentMode(originalTransaction.getPaymentMode());
		transactionRepository.save(refundTransaction);
		
		
		//notify this refund success message
		notificationServiceImpl.notifyPayee(originalTransaction.getPayee(), user, refundAmount, refundTransaction.getId(), true, "Refund processed Successfully.");
		
	}
}
