package com.paypal.gateway;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;
import com.paypal.service.strategy.MasterCardPaymentStrategy;
import com.paypal.service.strategy.PaymentStrategy;
import com.paypal.service.strategy.RupayPaymentStrategy;
import com.paypal.service.strategy.VisaPaymentStrategy;


@Component
public class DefaultPaymentGateway implements PaymentGateway{

	private static final Logger logger = LoggerFactory.getLogger(DefaultPaymentGateway.class);

	private final VisaPaymentStrategy visaPaymentStrategy;
	private final RupayPaymentStrategy rupayPaymentStrategy;
	private final MasterCardPaymentStrategy masterCardPaymentStrategy;
	
	 public DefaultPaymentGateway(List<PaymentStrategy> strategies) {
	        VisaPaymentStrategy visa = null;
	        MasterCardPaymentStrategy master = null;
	        RupayPaymentStrategy rupay = null;

	        for (PaymentStrategy strategy : strategies) {
	            if (strategy instanceof VisaPaymentStrategy) {
	                visa = (VisaPaymentStrategy) strategy;
	            } else if (strategy instanceof MasterCardPaymentStrategy) {
	                master = (MasterCardPaymentStrategy) strategy;
	            } else if (strategy instanceof RupayPaymentStrategy) {
	                rupay = (RupayPaymentStrategy) strategy;
	            }
	        }

	        this.visaPaymentStrategy = visa;
	        this.masterCardPaymentStrategy = master;
	        this.rupayPaymentStrategy = rupay;

	        logger.info("Loaded strategies: VISA={}, MasterCard={}, RuPay={}", visa != null, master != null, rupay != null);
	    }
	 
	 @Override
	 public void processPayment(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		 if (paymentMode == PaymentMode.CREDIT_CARD || paymentMode == PaymentMode.DEBIT_CARD) {
			 logger.info("Processing card payment: {}, {}", paymentMode, cardType);
			 
			 switch (cardType) {
			 case VISA:
				 if (visaPaymentStrategy != null) {
					 visaPaymentStrategy.pay(user, amount, payee, paymentMode, cardType);
				 }else {
					 throw new RuntimeException("VISA strategy not loaded");
				 }
				 break;
				 
			 case MASTERCARD:
				 if (masterCardPaymentStrategy != null) {
					 masterCardPaymentStrategy.pay(user, amount, payee, paymentMode, cardType);
				 }else {
					 throw new RuntimeException("MasterCard strategy not loaded");
				 }
				 break;
				 
			 case RUPAY:
				 if(rupayPaymentStrategy!=null) {
					 rupayPaymentStrategy.pay(user, amount, payee, paymentMode, cardType);
				 }else {
					 throw new RuntimeException("RuPay strategy not loaded");
				 }
				 break;
				 
			 default:
				 throw new IllegalArgumentException("Unsupported card type: " + cardType);
				 
			 }
			 
		 } else {
			 throw new IllegalArgumentException("Unsupported payment mode for card-type strategies: " + paymentMode); 
		 }
	    }
	}
	
//	private final Map<PaymentMode, PaymentStrategy> strategyMap;
//	
//	public DefaultPaymentGateway(List<PaymentStrategy> strategies) {
//		this.strategyMap = new EnumMap<>(PaymentMode.class);
//		for(PaymentStrategy strategy  : strategies) {
//			if(strategy instanceof CreditCardPaymentStrategy ) {
//				strategyMap.put(PaymentMode.CREDIT_CARD, strategy);
//			}else if(strategy instanceof UPIPaymentStrategy ) {
//				strategyMap.put(PaymentMode.UPI, strategy);
//			}else if(strategy instanceof DebitCardPaymentStrategy ) {
//				strategyMap.put(PaymentMode.DEBIT_CARD, strategy);
//			}
//		}
//
//		logger.info("Payment strategies loaded: {}", strategyMap.size());
//	}
//
//
//	@Override
//	public void processPayment(User user, double amount, Payee payee, PaymentMode paymentMode) {
//		PaymentStrategy strategy = strategyMap.get(paymentMode);
//		if(strategy == null) {
//			logger.error("Unsupported payment mode: {}", paymentMode);
//			throw new RuntimeException("Unsupported Payment mode: " + paymentMode);
//		}
//		
//		
//        // Log before executing the payment
//        logger.info("Executing payment strategy for mode: {}", paymentMode);
//
//		strategy.pay(user, amount, payee);
//		
//		// Log payment completion
//        logger.info("Payment completed for User: {}, Amount: Rs. {} to Payee: {} using Payment Mode: {}",
//                    user.getName(), amount, payee.getName(), paymentMode);
//	}
