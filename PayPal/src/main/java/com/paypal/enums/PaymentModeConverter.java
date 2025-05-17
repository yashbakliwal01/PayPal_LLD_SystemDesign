package com.paypal.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentModeConverter implements Converter<String, PaymentMode>{

	@Override
	public PaymentMode convert(String source) {
		return PaymentMode.fromString(source);
	}
}
