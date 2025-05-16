package com.paypal.enums;

import java.util.HashMap;
import java.util.Map;

public enum PaymentMode {

	CREDIT_CARD,
	DEBIT_CARD,
	UPI;
	
	private static final Map<String, PaymentMode> normalizedMap = new HashMap<>();
	
	static {
		for(PaymentMode mode : values()) {
			normalizedMap.put(normalize(mode.name()), mode);
		}
	}

	private static String normalize(String name) {
		return name.replaceAll("[_\\-\\s]", "").toUpperCase();
	}
	
	
	public static PaymentMode fromString(String input) {
		String normalizedInput = normalize(input);
		PaymentMode mode = normalizedMap.get(normalizedInput);
		if(mode==null) {
			throw new IllegalArgumentException("Invalid payment mode: " + input);
		}
		return mode;
	}
}
