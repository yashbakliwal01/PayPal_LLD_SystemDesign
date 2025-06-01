package com.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
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

	
	@PutMapping("/{id}/upiId")
	public String updateUpiId(@PathVariable Long id, @RequestParam String upiId) {
		User user = userRepository.findById(id)
									.orElseThrow(() -> new RuntimeException("User not found"));
		user.setUpiId(upiId);
		userRepository.save(user);
		return "UPI ID updated successfully for user " + user.getName();
		
	}
}
