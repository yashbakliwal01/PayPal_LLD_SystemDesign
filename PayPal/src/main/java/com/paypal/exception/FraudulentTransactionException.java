package com.paypal.exception;

public class FraudulentTransactionException extends BaseAppException{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FraudulentTransactionException(String message) {
		super(message);
	}
	
	public FraudulentTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

}
