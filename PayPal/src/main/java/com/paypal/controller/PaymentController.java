package com.paypal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.enums.PaymentMode;
import com.paypal.repository.PayeeRepository;
import com.paypal.repository.UserRepository;
import com.paypal.service.Impl.FraudDetectionService;
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
	
	@Autowired
	private FraudDetectionService fraudDetectionService;
	
	@PostMapping("/pay")
	public ResponseEntity<String> makePayment( @RequestParam Long userId, 
								@RequestParam Long payeeId, 
								@RequestParam double amount, 
								@RequestParam PaymentMode paymentMode) {
		
		Optional<User> optionalUser = userRepository.findById(userId);
	    if (optionalUser.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	    Optional<Payee> optionalPayee = payeeRepository.findById(payeeId);
	    if (optionalPayee.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payee not found");
	    }
		
//		User user = userRepository.findById(userId)
//									.orElseThrow(()-> new RuntimeException("User not found"));
//		Payee payee = payeeRepository.findById(payeeId)
//									.orElseThrow(()-> new RuntimeException("Payee not found"));
		
	    
	    User user = optionalUser.get();
	    Payee payee = optionalPayee.get();
	    
		//fraud check
		if(fraudDetectionService.isFraudulentTransaction(user, amount, payee)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Transaction blocked due to suspected fraud.");
		}
//		PaymentMode mode = PaymentMode.fromString(paymentMode);
		paymentService.makePayment(user, amount, payee, paymentMode);
//		return "Payment of Rs. " + amount + " using " + paymentMode + " was successful!";
		return ResponseEntity.ok("Payment processed successfully.");
	}

}
