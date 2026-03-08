package com.paypal.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.paypal.enums.PaymentMode;
import com.paypal.enums.TransactionStatus;
import com.paypal.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="transactions")
public class Transaction {
// Ledger System (Transaction history with balance tracking)
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private double amount;
	
	@CreationTimestamp
	private LocalDateTime timestamp;
	
	@Enumerated(EnumType.STRING) 
	private PaymentMode paymentMode;
	
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "payee_id", nullable = false)
    private Payee payee;

    @Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType transactionType;
    
    @Column(name = "transaction_ref", unique = true, nullable = false, updatable = false)
    private String transactionRef;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
}