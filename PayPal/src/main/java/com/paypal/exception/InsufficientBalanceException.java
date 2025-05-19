package com.paypal.exception;

public class InsufficientBalanceException extends BaseAppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException(String message) {
		super(message);
	}

	public InsufficientBalanceException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
