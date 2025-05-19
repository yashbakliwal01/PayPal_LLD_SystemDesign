package com.paypal.exception;

public abstract class BaseAppException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseAppException(String message) {
		super(message);
	}
	
	public BaseAppException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
