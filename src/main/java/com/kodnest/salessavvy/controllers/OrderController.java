package com.kodnest.salessavvy.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.services.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> getOrdersForUser(HttpServletRequest request) {
		try {
			User authenticatedUser = (User) request.getAttribute("authenticatedUser");
			
			if(authenticatedUser == null) {
				return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
			}
			Map<String, Object> response = orderService.getOrdersForUser(authenticatedUser);
			
			return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
		}
	}
}
