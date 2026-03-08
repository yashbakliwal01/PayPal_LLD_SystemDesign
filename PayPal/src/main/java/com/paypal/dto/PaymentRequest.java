package com.paypal.dto;

import com.paypal.enums.CardType;
import com.paypal.enums.PaymentMode;

import lombok.Data;

@Data
public class PaymentRequest {
	private String payerIdentifier; // Could be email or upi or phone number
	private String payeeIdentifier; // Could be email or upi or phone number
	
	private double amount;
	private PaymentMode paymentMode;
	private CardType cardType; // Optional, only for card payments
}
