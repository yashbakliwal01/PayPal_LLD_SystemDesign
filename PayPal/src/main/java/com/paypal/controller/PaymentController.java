package com.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.PaymentMode;
import com.paypal.repository.PayeeRepository;
import com.paypal.repository.UserRepository;
import com.paypal.service.Impl.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PayeeRepository payeeRepository;
	
	@PostMapping
	public String makePayment( @RequestParam Long userId, 
								@RequestParam Long payeeId, 
								@RequestParam double amount, 
								@RequestParam PaymentMode paymentMode) {
		
		User user = userRepository.findById(userId)
									.orElseThrow(()-> new RuntimeException("User not found"));
		Payee payee = payeeRepository.findById(payeeId)
									.orElseThrow(()-> new RuntimeException("Payee not found"));
		
//		PaymentMode mode = PaymentMode.fromString(paymentMode);
		paymentService.makePayment(user, amount, payee, paymentMode);
		return "Payment of Rs. " + amount + " using " + paymentMode + " was successful!";
	}

}
