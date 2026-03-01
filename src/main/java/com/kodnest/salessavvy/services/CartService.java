package com.kodnest.salessavvy.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.CartItem;
import com.kodnest.salessavvy.entities.Product;
import com.kodnest.salessavvy.entities.ProductImage;
import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.repositories.CartRepository;
import com.kodnest.salessavvy.repositories.ProductImageRepository;
import com.kodnest.salessavvy.repositories.ProductRepository;
import com.kodnest.salessavvy.repositories.UserRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductImageRepository productImageRepo;
	
	public Map<String, Object> getCartItems(int userId) {
		List<CartItem> cartItems = cartRepo.findCartItemsWithProductDetails(userId);
		
		Map<String, Object> response = new HashMap<>();
		User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		response.put("username", user.getUsername());
		response.put("role", user.getRole().toString());
		
		List<Map<String, Object>> products = new ArrayList<>();
		int overallTotalPrice = 0;
		
		for(CartItem cartItem : cartItems) {
			Map<String, Object> productDetails = new HashMap<>();
			
			Product product = cartItem.getProduct();
			
			List<ProductImage> productImages = productImageRepo.findByProduct_ProductId(product.getProductId());
			String imageUrl = (productImages != null && !productImages.isEmpty()) ? productImages.get(0).getImageUrl() : "default-image-url";
			
			productDetails.put("product_id", product.getProductId());
			productDetails.put("image_url", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());
			
			products.add(productDetails);
			
			overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
		}
		
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_totla_price", overallTotalPrice);
		
		response.put("cart", cart);
		
		return response;
	}

	public void addToCart(Integer userId, int productId, int quantity) {
		
		User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
		
		Product product = productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
		
		Optional<CartItem> existingItem = cartRepo.findByUserAndProduct(userId, productId);
		
		if(existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartRepo.save(cartItem);
		} else {
			CartItem newItem = new CartItem(user, product, quantity);
			cartRepo.save(newItem);
		}
	}
	
	public void updateCartItemQuantity(int userId, int productId, int quantity) {
		User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Product product = productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
		
		Optional<CartItem> existingItem = cartRepo.findByUserAndProduct(userId, productId);
		
		if(existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			if(quantity == 0) {
				deleteCartItem(userId, productId);
			} else {
				cartItem.setQuantity(quantity);
				cartRepo.save(cartItem);
			}
		}
	}
	
	public void deleteCartItem(int userId, int productId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Product product = productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
		
		cartRepo.deleteCartItem(userId, productId);
	}
}
