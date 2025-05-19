package com.paypal.entity;

import java.time.LocalDateTime;

import com.paypal.enums.PaymentMode;

import lombok.Data;

@Data
public class TransactionDTO {
	
	 private Long id;
	 private double amount;
	 private PaymentMode paymentMode;
	 private LocalDateTime timestamp;
	 
	 public TransactionDTO(Long id, double amount, PaymentMode paymentMode, LocalDateTime timestamp) {
		super();
		this.id = id;
		this.amount = amount;
		this.paymentMode = paymentMode;
		this.timestamp = timestamp;
		
	 }
}
