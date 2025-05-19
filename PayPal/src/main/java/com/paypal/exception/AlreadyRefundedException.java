package com.paypal.exception;

public class AlreadyRefundedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyRefundedException(String message) {
        super(message);
    }
}
