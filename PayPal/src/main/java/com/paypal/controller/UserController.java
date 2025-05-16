package com.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	@PutMapping("/{id}/upiId")
	public String updateUpiId(@PathVariable Long id, @RequestParam String upiId) {
		User user = userRepository.findById(id)
									.orElseThrow(() -> new RuntimeException("User not found"));
		user.setUpiId(upiId);
		userRepository.save(user);
		return "UPI ID updated successfully for user " + user.getName();
		
	}
}
