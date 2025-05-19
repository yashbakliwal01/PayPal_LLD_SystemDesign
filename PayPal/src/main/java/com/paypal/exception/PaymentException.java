package com.paypal.exception;

public class PaymentException extends BaseAppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// used to keep track of the version of a class when saving and loading objects (called serialization).
	
	
	
	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String message, Throwable cause) {
		super(message, cause);
	}
}
	