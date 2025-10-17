package com.paypal.exception;

public class AlreadyRefundedException extends BaseAppException {

    private static final long serialVersionUID = 1L;

    public AlreadyRefundedException(String message) {
        super(message);
    }

    public AlreadyRefundedException(String message, Throwable cause) {
        super(message, cause);
    }
}
