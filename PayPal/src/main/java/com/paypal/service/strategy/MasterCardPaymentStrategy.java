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
public class MasterCardPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MasterCardPaymentStrategy.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void pay(User user, double amount, Payee payee, PaymentMode paymentMode, CardType cardType) {

        logger.info("Initiating Mastercard payment | user={} | payee={} | amount={}", user.getName(), payee.getName(), amount);

        // Step 1: Validate payment details
        validatePaymentDetails(user, payee, amount);

        try {

            // Step 2: create payload
            String payload = createPaymentRequestPayload(user, amount, payee, paymentMode, cardType);
            logger.debug("Payment request payload: {}", payload);

            
            // Step 3: call gateway
            String gatewayResponse = callPaymentGateway(payload);

           
            // Step 4: check gateway response
            boolean success = processGatewayResponse(gatewayResponse);

            if (success) {
                logger.info("Mastercard payment successful for user={}", user.getName());
            } else {
                logger.error("Mastercard payment failed for user {}", user.getName());
                throw new RuntimeException("Payment failed at payment gateway");
            }
        } catch (Exception ex) {
            logger.error("Error during Mastercard payment", ex);
            throw new RuntimeException("Mastercard payment failed", ex);
        }
    }

    private boolean processGatewayResponse(String response) {
    	logger.debug("Processing gateway response: {}", response);
    	return response.contains("\"status\": \"success\"");
    }

    private String callPaymentGateway(String payload) {
        logger.debug("Calling Mastercard payment gateway...");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gateway call interrupted", e);
        }

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

            return objectMapper.writeValueAsString(payload);

        } catch (Exception e) {

            logger.error("Error creating payment payload", e);
            throw new RuntimeException("Failed to build payment payload", e);

        }
    }

    private void validatePaymentDetails(User user, Payee payee, double amount) {

        if (user == null)
            throw new IllegalArgumentException("User must not be null");

        if (payee == null)
            throw new IllegalArgumentException("Payee must not be null");

        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");

        if (amount > 100000) {
            logger.warn("Amount exceeds Mastercard limit");
            throw new IllegalArgumentException("Amount exceeds transaction limit");
        }
    }
}