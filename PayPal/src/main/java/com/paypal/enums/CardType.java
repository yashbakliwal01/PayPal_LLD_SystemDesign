package com.paypal.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

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
	
	@JsonCreator 
	// This annotation allows Jackson to use this method for deserialization
	public static CardType fromString(String input) { 
		
		if(input == null) return null;
		
		String normalizedInput = normalize(input);
		CardType type = normalizedCardTypeMap.get(normalizedInput);
		if(type==null) {
			throw new IllegalArgumentException("Invalid card type: " + input);
        }
        return type;
	}
}
