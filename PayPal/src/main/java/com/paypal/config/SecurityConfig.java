package com.paypal.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.paypal.utility.security.CustomUserDetailsService;
import com.paypal.utility.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private CustomUserDetailsService customeUserDetailsService;
	
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http.csrf(csrf->csrf.disable())
    		   .authorizeHttpRequests(auth->auth
    				   .requestMatchers("/api/auth/**", "/api/users/register").permitAll()
    				   
    				   //Only for ADMIN access where he can use all users and managing system
    				   .requestMatchers("/api/admin/**").hasRole("ADMIN")
    				   
    				   .requestMatchers("/api/transactions/**").hasRole("USER")
    				   .requestMatchers("/api/wallet/**").hasRole("USER")
    				   .requestMatchers("/api/payments/**").hasRole("USER")
    				  
    				   .anyRequest().authenticated())
    		   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		   .authenticationProvider(daoAuthProvider())
    		   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    		   .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customeUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}
    
}

