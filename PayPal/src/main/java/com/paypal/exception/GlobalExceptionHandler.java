package com.paypal.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		logger.warn("Insufficient balance: {}", ex.getMessage());
		return new ResponseEntity<>("Insufficient balance. Please add funds to proceed.", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FraudulentTransactionException.class)
	public ResponseEntity<String> handleFraudulentTransactionException(FraudulentTransactionException ex) {
		logger.warn("Fraudulent transaction blocked: {}", ex.getMessage());
		return new ResponseEntity<>("Transaction blocked due to suspicious activity.", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<String> handlePaymentException(PaymentException ex) {
		logger.error("Payment failed: {}", ex.getMessage());
		return new ResponseEntity<>("Payment processing failed. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		logger.error("Unhandled exception: ", ex);
		return new ResponseEntity<>("Something went wrong. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
		List<String> details = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());

		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Validation failed",
				request.getDescription(false)
		);

		response.setDetails(details);
		logger.error("Validation Error: {}", details);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
