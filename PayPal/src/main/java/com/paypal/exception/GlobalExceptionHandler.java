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

    // Handles insufficient balance errors
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex, WebRequest request) {
        logger.warn("Insufficient balance: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Insufficient balance. Please add funds to proceed.", request);
    }

    // Handles already refunded transaction errors
    @ExceptionHandler(AlreadyRefundedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyRefundedException(AlreadyRefundedException ex, WebRequest request) {
        logger.warn("Refund already processed: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, "This transaction has already been refunded.", request);
    }

    // Handles fraudulent transactions
    @ExceptionHandler(FraudulentTransactionException.class)
    public ResponseEntity<ErrorResponse> handleFraudulentTransactionException(FraudulentTransactionException ex, WebRequest request) {
        logger.warn("Fraudulent transaction blocked: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, "Transaction blocked due to suspicious activity.", request);
    }

    // Handles general payment failures
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex, WebRequest request) {
        logger.error("Payment failed: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing failed. Please try again later.", request);
    }

    // Handles validation errors from @Valid annotations
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

    // Handles any unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred: ", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.", request);
    }

    // Utility method to build structured ErrorResponse
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, String message, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, status);
    }
}
