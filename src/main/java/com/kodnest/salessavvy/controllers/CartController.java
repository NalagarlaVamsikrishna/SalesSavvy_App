package com.kodnest.salessavvy.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.repositories.UserRepository;
import com.kodnest.salessavvy.services.CartService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping("/add")
	@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
		
		User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: "+ username));
		
		cartService.addToCart(user.getUserId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/items")
	public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
		// Fetch user by username to get userId
		User user = (User) request.getAttribute("authenticatedUser");
		
		// Call service to get cart items for user
		Map<String, Object> cartItems = cartService.getCartItems(user.getUserId());
		return ResponseEntity.ok(cartItems);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		int quantity = (int) request.get("quantity");
		
		User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		
		cartService.updateCartItemQuantity(user.getUserId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		
		cartService.deleteCartItem(user.getUserId(), productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
