package com.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.LoginRequest;
import com.paypal.utility.JwtUtil;
import com.paypal.utility.security.CustomUserDetails;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request){
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			String setCookie = jwtUtil.generateJwtCookie(userDetails.getUsername()).toString();
			
			return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, setCookie)
				.body(Map.of("message", "Login Successful", "email", userDetails.getUsername()));
		}catch(AuthenticationException e) {
			return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password!"));
		}catch(Exception e) {
			return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(Authentication authentication){
	    if(authentication == null || !authentication.isAuthenticated()){
	        return ResponseEntity.status(401)
	            .body(Map.of("error", "Unauthorized"));
	    }

	    String setCookie = jwtUtil.getCleanJwtCookie().toString();
	    return ResponseEntity.ok()
	        .header(HttpHeaders.SET_COOKIE, setCookie)
	        .body(Map.of("message", "Logged out successfully"));
	}

	 

}