package com.paypal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(length = 10, unique = true, nullable = false)
	private String phone;
	
	@Column(name = "upi_id")
	private String upiId;

	 @OneToOne(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL)
	 private Wallet wallet;
}
