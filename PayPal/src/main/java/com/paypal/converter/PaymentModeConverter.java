package com.paypal.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.paypal.enums.PaymentMode;

@Component
public class PaymentModeConverter implements Converter<String, PaymentMode>{

	@Override
	public PaymentMode convert(String source) {
		if(source==null || source.trim().isEmpty()) {
			return null;
		}
		
		return PaymentMode.fromString(source);
	}
}
