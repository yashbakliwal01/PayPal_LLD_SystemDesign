package com.paypal.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.paypal.enums.CardType;

@Component
public class CardTypeConverter implements Converter<String, CardType>{

	@Override
	public CardType convert(String value) {
		
		if(value==null || value.trim().isEmpty()) {
			return null;
		}
		
		return CardType.fromString(value);
	}
}
