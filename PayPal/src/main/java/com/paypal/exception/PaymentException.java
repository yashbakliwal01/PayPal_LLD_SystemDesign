package com.paypal.exception;

public class PaymentException extends BaseAppException{

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String message, Throwable cause) {
		super(message, cause);
	}
}
	