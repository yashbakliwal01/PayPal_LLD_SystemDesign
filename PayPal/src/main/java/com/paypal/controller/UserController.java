package com.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.User;
import com.paypal.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
	    if (userRepository.existsByEmail(user.getEmail())) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
	    }

	    // Encode password before saving
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    userRepository.save(user);
	    return ResponseEntity.ok(Map.of("message", "User registered successfully"));
	}

	
	@PutMapping("/upiId")
	public ResponseEntity<?> updateUpiId(@RequestParam String upiId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
			return ResponseEntity.status(401).body(Map.of("error", "You must be logged in to update UPI ID"));
		}
		
		String currentUserEmail = auth.getName();
		
		User user = userRepository.findByEmail(currentUserEmail)
									.orElseThrow(() -> new RuntimeException("User not found"));
		
		//validate the upi
		if(!upiId.contains("@")) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid UPI ID format"));
		}
		
		//update the userId
		user.setUpiId(upiId);
		userRepository.save(user);
		
		return ResponseEntity.ok(Map.of("message", "UPI ID updated successfully"));
		
	}
}
