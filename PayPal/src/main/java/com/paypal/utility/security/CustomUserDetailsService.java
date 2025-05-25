package com.paypal.utility.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paypal.entity.User;
import com.paypal.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String emailOrUsername
			) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(emailOrUsername)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrUsername));
		return new CustomUserDetails(user);
	}

}
