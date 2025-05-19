package com.paypal.entity;

import java.time.LocalDateTime;

import com.paypal.enums.PaymentMode;

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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public PaymentMode getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
