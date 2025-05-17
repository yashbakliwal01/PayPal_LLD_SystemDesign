package com.paypal.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {

	private LocalDateTime timeStamp;
	private int status;
	private String error;
	private String message;
	private String path;
	private List<String> details;
	
	public ErrorResponse(LocalDateTime timeStamp, int status, String error, String message, String path) {
		super();
		this.timeStamp = timeStamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}
	
	
}
