package com.kodnest.salessavvy.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.Role;
import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.repositories.JWTTokenRepository;
import com.kodnest.salessavvy.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminUserService {

	private final UserRepository userRepo;
	
	private final JWTTokenRepository jwtTokenRepo;
	
	public AdminUserService(UserRepository userRepo, JWTTokenRepository jwtTokenRepo) {
		this.userRepo = userRepo;
		this.jwtTokenRepo = jwtTokenRepo;
	}
	
	@Transactional
	public User modifyUser(Integer userId, String username, String email, String role) {
		Optional<User> userOptional = userRepo.findById(userId);
		if(userOptional.isEmpty()) {
			throw new IllegalArgumentException("User not found");
		}
		User existingUser = userOptional.get();
		
		if(username != null && !username.isEmpty()) {
			existingUser.setUsername(username);
		}
		
		if(email != null && !email.isEmpty()) {
			existingUser.setEmail(email);
		}
		
		if(role != null && !role.isEmpty()) {
			try {
				existingUser.setRole(Role.valueOf(role));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid role: " + role);
			}
		}
		
		jwtTokenRepo.deleteByUserId(userId);
		return userRepo.save(existingUser);		
	}
	
	public User getUserById(Integer userId) {
		return userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}
 }
