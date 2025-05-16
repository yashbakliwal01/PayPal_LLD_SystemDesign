package com.paypal.gateway;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.PaymentMode;
import com.paypal.service.strategy.CreditCardPaymentStrategy;
import com.paypal.service.strategy.DebitCardPaymentStrategy;
import com.paypal.service.strategy.PaymentStrategy;
import com.paypal.service.strategy.UPIPaymentStrategy;


@Component
public class DefaultPaymentGateway implements PaymentGateway{

	private static final Logger logger = LoggerFactory.getLogger(DefaultPaymentGateway.class);

	
	private final Map<PaymentMode, PaymentStrategy> strategyMap;
	
	public DefaultPaymentGateway(List<PaymentStrategy> strategies) {
		this.strategyMap = new EnumMap<>(PaymentMode.class);
		for(PaymentStrategy strategy  : strategies) {
			if(strategy instanceof CreditCardPaymentStrategy ) {
				strategyMap.put(PaymentMode.CREDIT_CARD, strategy);
			}else if(strategy instanceof UPIPaymentStrategy ) {
				strategyMap.put(PaymentMode.UPI, strategy);
			}else if(strategy instanceof DebitCardPaymentStrategy ) {
				strategyMap.put(PaymentMode.DEBIT_CARD, strategy);
			}
		}

		logger.info("Payment strategies loaded: {}", strategyMap.size());
	}


	@Override
	public void processPayment(User user, double amount, Payee payee, PaymentMode paymentMode) {
		PaymentStrategy strategy = strategyMap.get(paymentMode);
		if(strategy == null) {
			logger.error("Unsupported payment mode: {}", paymentMode);
			throw new RuntimeException("Unsupported Payment mode: " + paymentMode);
		}
		
		
        // Log before executing the payment
        logger.info("Executing payment strategy for mode: {}", paymentMode);

		strategy.pay(user, amount, payee);
		
		// Log payment completion
        logger.info("Payment completed for User: {}, Amount: Rs. {} to Payee: {} using Payment Mode: {}",
                    user.getName(), amount, payee.getName(), paymentMode);
	}

}
