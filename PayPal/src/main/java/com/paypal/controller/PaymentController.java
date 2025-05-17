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
import com.paypal.enums.CardType;
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
	public ResponseEntity<String> makePayment( @RequestParam Long userId, @RequestParam Long payeeId, 
			@RequestParam double amount, @RequestParam PaymentMode paymentMode,  @RequestParam(required=false) CardType cardType) {
		
		Optional<User> optionalUser = userRepository.findById(userId);
	    if (optionalUser.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	    Optional<Payee> optionalPayee = payeeRepository.findById(payeeId);
	    if (optionalPayee.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payee not found");
	    }
		
	    User user = optionalUser.get();
	    Payee payee = optionalPayee.get();
	    
		//fraud check
		if(fraudDetectionService.isFraudulentTransaction(user, amount, payee)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Transaction blocked due to suspected fraud.");
		}
		
		
		// Validate cardType only for CREDIT_CARD or DEBIT_CARD
		if((paymentMode == paymentMode.CREDIT_CARD || paymentMode == paymentMode.DEBIT_CARD) && cardType==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card type must be specified for card payments.");
		}
		
		paymentService.makePayment(user, amount, payee, paymentMode, cardType);
		return ResponseEntity.ok("Payment processed successfully using " + paymentMode + (cardType!=null?" ("+cardType+")" : "") +"." );
	}

}

//api: http://localhost:8081/api/payments/pay?userId=103&payeeId=1003&amount=1&paymentMode=debit card&cardType=visa
