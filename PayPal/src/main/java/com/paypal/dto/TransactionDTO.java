package com.paypal.dto;

import java.time.LocalDateTime;

import com.paypal.enums.PaymentMode;
import com.paypal.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
	
	private Long id;
    private double amount;
    private PaymentMode paymentMode;
    private TransactionType transactionType;
    private LocalDateTime timestamp;
}
