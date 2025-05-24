package com.paypal.enums.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.paypal.enums.CardType;


@Component
public class CardTypeConvertor implements Converter<String, CardType>{

	@Override
	public CardType convert(String value) {
		return CardType.fromString(value);
	}
}
