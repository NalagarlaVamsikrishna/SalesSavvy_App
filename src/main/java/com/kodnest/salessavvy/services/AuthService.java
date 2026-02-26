package com.kodnest.salessavvy.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.JWTToken;
import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.repositories.JWTTokenRepository;
import com.kodnest.salessavvy.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
	
	private final Key SIGNING_KEY;
	
	private final UserRepository userRepo;
	
	private final JWTTokenRepository jwtTokenRepo;
	
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public AuthService(UserRepository userRepo, JWTTokenRepository jwtTokenRepo,
			@Value("${jwt.secret}") String jwtSecret) {
		this.userRepo = userRepo;
		this.jwtTokenRepo = jwtTokenRepo;
		this.passwordEncoder = new BCryptPasswordEncoder();
		
		if(jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
			throw new IllegalArgumentException("JWT_SECRET in application.properties must be at least 64 bytes long for HS512.");
		}
		
		this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));	
	}
	
	public User authenticate(String username, String password) {
		User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid username or password"));
		
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid username or password");
		}
		return user;
	}
	
	public String generateToken(User user) {
		String token;
		LocalDateTime now = LocalDateTime.now();
		JWTToken existingToken = jwtTokenRepo.findByUserId(user.getUserId());
		
		if(existingToken != null && now.isBefore(existingToken.getExpiresAt())) {
			token = existingToken.getToken();
		} 
		else {
			token = generateNewToken(user);
			
			if(existingToken != null) {
				jwtTokenRepo.delete(existingToken);
			}
			saveToken(user, token);
		}
		return token;
	}

	public String generateNewToken(User user) {
		
		return Jwts.builder().setSubject(user.getUsername()).claim("role", user.getRole().name()).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(SIGNING_KEY, SignatureAlgorithm.HS512).compact();
	}
	
	public void saveToken(User user, String token) {
		JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));
		jwtTokenRepo.save(jwtToken);
		
	}

	
	
	
}
