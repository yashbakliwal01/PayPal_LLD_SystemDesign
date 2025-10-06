package com.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.LoginRequest;
import com.paypal.utility.JwtUtil;
import com.paypal.utility.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			response.addHeader("Set-Cookie", jwtUtil.generateJwtCookie(userDetails.getUsername()).toString());
			return ResponseEntity.ok(Map.of("message", "Login Successful", "email", userDetails.getUsername()));
		}catch(Exception e) {
			return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password!"));
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response){
		response.addHeader("Set-Cookie", jwtUtil.getCleanJwtCookie().toString());
		return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
	}
		 

}
