package com.paypal.exception;

public class InsufficientBalanceException extends BaseAppException{

	public InsufficientBalanceException(String message) {
		super(message);
	}

	public InsufficientBalanceException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
