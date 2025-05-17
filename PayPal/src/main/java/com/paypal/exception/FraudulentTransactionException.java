package com.paypal.exception;

public class FraudulentTransactionException extends BaseAppException{


	public FraudulentTransactionException(String message) {
		super(message);
	}
	
	public FraudulentTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

}
