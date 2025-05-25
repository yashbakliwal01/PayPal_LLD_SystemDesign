package com.paypal.utility;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	@Value("${paypal.jwt.secret}")
	private String secretKey;
	
	
	@Value("${paypal.jwt.expiration}")
	private long jwtExpirationMs;
	
	@Value("${paypal.jwt.cookieName}")
	private String jwtCookie;
	
	private Key key;
	
	@PostConstruct
	public void init() {
		if(secretKey==null || secretKey.isEmpty()) {
			throw new IllegalArgumentException("JWT secret key is not configured properly!");
		}
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validToken(String token, String username) {
		try {
			String extractedUsername = extractUsername(token);
			return (username.equals(extractedUsername) && !isTokenExpired(token));
			
		}catch(Exception e) {
			logger.error("JWT token validation error: {}", e.getMessage());
			return false;
		}
	}

	private boolean isTokenExpired(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getExpiration()
				.before(new Date());
	}

	private String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	//cookies specific methods
	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		return cookie != null ? cookie.getValue() : null;
	}
	
	public ResponseCookie generateJwtCookie(String username) {
		String jwt = generateToken(username);
		return ResponseCookie.from(jwtCookie, jwt)
				.path("/").maxAge(jwtExpirationMs/1000)
				.httpOnly(true).build();
	}
	
	public ResponseCookie getCleanJwtCookie() {
		return ResponseCookie.from(jwtCookie, "")
				.path("/").maxAge(jwtExpirationMs/1000)
				.httpOnly(true).build();
	}
	
}

