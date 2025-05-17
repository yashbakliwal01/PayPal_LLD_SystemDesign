package com.paypal.enums;

import java.util.HashMap;
import java.util.Map;

public enum CardType {
	VISA,
	MASTERCARD,
	RUPAY;
	
	public static final Map<String, CardType> normalizedCardTypeMap = new HashMap<>();
	
	static {
		for(CardType c : values()) {
			normalizedCardTypeMap.put(normalize(c.name()), c);
		}
	}

	public static String normalize(String name) {
		return name.replaceAll("[_\\-\\s]", "").toUpperCase();
	}
	
	
	public static CardType fromString(String input) { 
		String normalizedInput = normalize(input);
		CardType type = normalizedCardTypeMap.get(normalizedInput);
		if(type==null) {
			throw new IllegalArgumentException("Invalid card type: " + input);
        }
        return type;
	}
}
