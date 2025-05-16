package com.paypal.entity;

import java.time.LocalDateTime;

import com.paypal.enums.PaymentMode;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="transactions")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double amount;
	private LocalDateTime timestamp;
	private boolean isRefund;
	
	@Enumerated(EnumType.STRING) 
	private PaymentMode paymentMode;
	
    @ManyToOne 
    private User user;
    
    @ManyToOne 
    private Payee payee;
}