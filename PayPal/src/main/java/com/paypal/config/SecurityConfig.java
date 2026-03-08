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

import com.paypal.security.CustomUserDetailsService;
import com.paypal.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
    		   .csrf(csrf->csrf.disable())
    		   .cors(cors->{})
    		   .authorizeHttpRequests(auth->auth
    				   .requestMatchers(
    					        "/api/auth/**",
    					        "/api/users/register",
    					        "/error"
    					).permitAll()
    				   
    				   //Only for ADMIN access where he can use all users and managing system
    				   .requestMatchers("/api/admin/**").hasRole("ADMIN")
    				   .requestMatchers("/api/users/**").hasRole("USER")
    				   .requestMatchers("/api/transactions/**").hasRole("USER")
    				   .requestMatchers("/api/wallet/**").hasRole("USER")
    				   .requestMatchers("/api/payments/**").hasRole("USER")
    				  
    				   .anyRequest().authenticated())
    		   .exceptionHandling(exception -> exception
    				    .authenticationEntryPoint((request, response, authException) -> {
    				        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				        response.setContentType("application/json");

    				        response.getWriter().write("""
    				        {
    				          "status": "failed",
    				          "message": "Please login with your email and password to update your account information."
    				        }
    				        """);
    				    })
    			)
    		   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		   .authenticationProvider(daoAuthProvider())
    		   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    		   .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
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

