package com.paypal.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class CardTypeConvertor implements Converter<String, CardType>{

	@Override
	public CardType convert(String value) {
		return CardType.fromString(value);
	}
}
