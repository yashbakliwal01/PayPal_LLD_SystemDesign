package com.paypal.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.dto.UserRegisterDTO;
import com.paypal.entity.User;
import com.paypal.repository.UserRepository;

import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	//REGISTER NEW USER
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO request) {
		logger.info("Registration request received | email={} | phone={}", request.getEmail(), request.getPhone());
		
		//checks exists by email
		if (userRepository.existsByEmail(request.getEmail())) {
			logger.warn("Registration failed - email already exists | email={}", request.getEmail());
			return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
	    }
		
		// Check phone duplicate
	    if (userRepository.existsByPhone(request.getPhone())) {
	        logger.warn("Registration failed - Phone already exists | phone={}", request.getPhone());
	        return ResponseEntity.badRequest().body(
	                Map.of(
	                        "status", "failed",
	                        "message", "Phone number already registered"
	                ));
	    }

	    User user = new User();
	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setPhone(request.getPhone());
	    // Encode password before saving
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    userRepository.save(user);
	    
	    logger.info("User registered successfully | email={}", request.getEmail());
	    
	    return ResponseEntity.ok(Map.of(
	    		"message", "User registered successfully",
	    		"email", user.getEmail()
	    		));
	}
	
	
	//Update the upiId
	@PutMapping("/upiId")
	public ResponseEntity<?> updateUpiId(@RequestParam String upiId) {
		
		logger.info("UPI update request received | upiId={}", upiId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
			logger.warn("Unauthorized attempt to update UPI");
			return ResponseEntity.status(401).body(Map.of(
					"status", "failed",
					"message", "You must be logged in to update UPI ID"));
		}
		
		String currentUserEmail = auth.getName();
		logger.info("Authenticated user | email={}", currentUserEmail);
		
		User user = userRepository.findByEmail(currentUserEmail)
									.orElseThrow(() -> {
										logger.error("User not found in database | email={}", currentUserEmail);
										return new RuntimeException("User not found");
									});
		
		
		//validate the upi
		if(!upiId.contains("@")) {
			logger.error("User not found | email={}", currentUserEmail);
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid UPI ID format"));
		}
		
		//update the userId
		user.setUpiId(upiId);
		userRepository.save(user);
		
		logger.info("UPI ID updated successfully | user={} | newUpiId={}", currentUserEmail, upiId);
		
		return ResponseEntity.ok(Map.of("message", "UPI ID updated successfully"));
	}
}
