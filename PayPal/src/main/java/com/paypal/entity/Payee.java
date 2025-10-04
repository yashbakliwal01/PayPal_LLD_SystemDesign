package com.paypal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name="payee")
public class Payee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank(message = "Name is mandatory")
    private String name;
	
	@Email
	@Column(unique = true, nullable = false)
	private String email;
	
	@Pattern(regexp = "^[0-9]{10}$", message = "Must be exactly 10 digits")
	@Column(unique = true, nullable = false)
	private String phoneNumber;
	
	@Pattern(regexp = "^[A-Z0-9]{1,16}$", message = "Account number must be uppercase letters and digits only, up to 16 characters")
	@Column(unique = true, nullable = false)
	private String accountIdentifier;
	
	@Column(name="upi_id")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid UPI ID format")
	@NotBlank(message = "UPI ID is mandatory")
	private String upiId;
}
