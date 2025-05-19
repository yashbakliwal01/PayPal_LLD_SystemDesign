package com.paypal.entity;

import lombok.Data;

@Data
public class WalletDTO {
    
	private Long id;
    private Long userId;
    private double balance;

    public WalletDTO(Long id, Long userId, double balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }
}