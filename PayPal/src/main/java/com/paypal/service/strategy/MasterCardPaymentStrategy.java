package com.paypal.service.strategy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

@Component
public class MasterCardPaymentStrategy implements PaymentStrategy{
	
	private final Logger logger = LoggerFactory.getLogger(MasterCardPaymentStrategy.class);
	
	@Override
	public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		
		logger.info("Initiating Mastercard payment for user '{}' to payee '{}', amount Rs. {}", user.getName(), payee.getName(), amount);

	        // Step 1: Validate payment details
	        validatePaymentDetails(user, payee, amount);
        
		try {
			//step 2: payment request payload
			String paymentRequestPayLoad = createPaymentRequestPayload(user, amount, payee, paymentMode, cardType);
			logger.debug("Payment request payload: {}", paymentRequestPayLoad);
			
			// Step 3: Call external payment gateway API
           		 String paymentGatewayResponse = callPaymentGateway(paymentRequestPayLoad);

	                // Step 4: Process payment gateway response
	                boolean success = processGatewayResponse(paymentGatewayResponse);
			if(success) {
				logger.info("Payment successful for user '{}' to payee '{}'. Amount: Rs. {}", user.getName(), payee.getName(), amount);
			}else {
				logger.error("Payment failed as per gateway response for user '{}'", user.getName());
				throw new RuntimeException("Payment processing failed at the payment gateway.");
			}
		}catch(Exception ex) {
			logger.error("Exception during Mastercard payment processing: {}", ex.getMessage(), ex);
           		 // Optionally handle rollback or compensating transactions here
            		throw new RuntimeException("Payment failed due to system error. Please try again later.");
		}
	}

	private boolean processGatewayResponse(String response) {
		// Parse JSON response and check status - simplified here
       		 logger.debug("Processing payment gateway response: {}", response);

     	        // In real code, use a JSON parser like Jackson or Gson:
       		// JsonNode node = objectMapper.readTree(response);
        	// return "success".equalsIgnoreCase(node.get("status").asText());

       		 return response.contains("\"status\": \"success\"");
	}

	private String callPaymentGateway(String paymentRequestPayLoad) {
		logger.debug("Calling external Mastercard payment gateway API...");

        // Simulated response - in real life parse the HTTP response
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment gateway call interrupted", e);
        }

        // Simulated success JSON response
        return "{ \"status\": \"success\", \"transactionId\": \"TXN123456789\" }";
    }

	private String createPaymentRequestPayload(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {
		try {
			Map<String, Object> payload = new HashMap<>();
			payload.put("userId", user.getId());
			payload.put("userName", user.getName());
			payload.put("payeeId", payee.getId());
			payload.put("payeeName", payee.getName());
			payload.put("amount", amount);
			payload.put("paymentMode", paymentMode.name());
			payload.put("cardType", cardType.name());
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(payload);
			
		} catch (Exception e) {
			logger.error("Error while creating payment request payload", e);
	        throw new RuntimeException("Failed to build payment request payload");
		}
	}

	private void validatePaymentDetails(User user, Payee payee, double amount) {
		if(user==null) {
			logger.error("User is null. Cannot process payment.");
            throw new IllegalArgumentException("User must not be null");
		}
		
		if(payee==null) {
			logger.error("Payee is null. Cannot process payment.");
            throw new IllegalArgumentException("Payee must not be null");
		}
		
		if(amount<=0) {
			logger.error("Invalid amount: {}", amount);
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		if (amount > 100000) {
            logger.warn("Amount exceeds max limit for Mastercard payments");
            throw new IllegalArgumentException("Amount exceeds transaction limit");
        }
		
	}

}
