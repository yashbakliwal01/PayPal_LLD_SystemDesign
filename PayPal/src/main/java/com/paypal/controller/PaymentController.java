package com.paypal.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.dto.PaymentRequest;
import com.paypal.entity.Payee;
import com.paypal.entity.User;
import com.paypal.exception.AlreadyRefundedException;
import com.paypal.exception.TransactionNotFoundException;
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
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@PostMapping("/pay")
	public ResponseEntity<?> makePayment(@RequestBody PaymentRequest request) {
		
		logger.info("Payment request received for payee:: {}", request.getPayerIdentifier());
		
		Optional<User> payerOpt = userRepository.findByEmail(request.getPayerIdentifier());
		if(payerOpt.isEmpty()) {
			logger.warn("Payer not found: {}", request.getPayerIdentifier());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payer not found"));
		}
		
		
		Optional<Payee> payeeOpt = payeeRepository.findById(Long.valueOf(request.getPayeeIdentifier()));
		if (payeeOpt.isEmpty()) {
	        logger.warn("Payee not found: {}", request.getPayeeIdentifier());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payee not found"));
	    }
		
		User payer = payerOpt.get();
		Payee payee = payeeOpt.get();
		
		logger.info("Processing payment from {} to {} for amount {}", payer.getName(), payee.getName(), request.getAmount());
		
		paymentService.makePayment(payer, 
								   request.getAmount(), 
								   payee, 
								   request.getPaymentMode(), 
								   request.getCardType());
		
		logger.info(
			    "Payment of Rs.{} from {} to {} processed successfully using {}",
			    request.getAmount(),
			    payer.getName(),
			    payee.getName(),
			    request.getPaymentMode(),request.getCardType()
			);
		
		return ResponseEntity.ok(Map.of("status", "Payment Successful", "amount", request.getAmount()));
		
	}

	
	@PostMapping("/refund")
	public ResponseEntity<?> refundTransfer(@RequestParam String transactionRef, @RequestParam(defaultValue = "false") boolean confirm) {
		
		logger.info(">> Refund request received for transactionRef: {}", transactionRef);
		
	    try {
	        paymentService.refundTransaction(transactionRef, confirm);
	        
	        logger.info("Refund successful for transaction {}", transactionRef); 
	        return ResponseEntity.ok(Map.of(
	            "status", "success",
	            "transactionId", transactionRef,
	            "message", "Refund processed successfully."
	        ));
	    } catch (AlreadyRefundedException e) {
	    	
	    	logger.warn("Refund already done for {}", transactionRef);

	        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
	                "status", "failed",
	                "transactionRef", transactionRef,
	                "message", e.getMessage()
	        ));
	    } catch (TransactionNotFoundException e) {

	        logger.error("Refund failed. Transaction not found {}", transactionRef);

	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
	                "status", "failed",
	                "transactionRef", transactionRef,
	                "message", e.getMessage()
	        ));

	    }catch (Exception e) {
	    	logger.error("Refund failed for transaction {}", transactionRef, e);
	    	
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	            "status", "failed",
	            "message", "Refund processing failed: " + e.getMessage()
	        ));
	    }
	}
}
