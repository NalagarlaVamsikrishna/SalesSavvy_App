package com.kodnest.salessavvy.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.OrderItem;
import com.kodnest.salessavvy.entities.Product;
import com.kodnest.salessavvy.entities.ProductImage;
import com.kodnest.salessavvy.entities.User;
import com.kodnest.salessavvy.repositories.OrderItemRepository;
import com.kodnest.salessavvy.repositories.ProductImageRepository;
import com.kodnest.salessavvy.repositories.ProductRepository;

@Service
public class OrderService {

	@Autowired
	private OrderItemRepository orderItemRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductImageRepository productImageRepo;
	
	public Map<String, Object> getOrdersForUser(User user) {
		List<OrderItem> orderItems = orderItemRepo.findSuccessfulOrderByUserId(user.getUserId());
		
		Map<String, Object> response = new HashMap<>();
		response.put("username", user.getUsername());
		response.put("role", user.getRole());
		
		List<Map<String, Object>> products = new ArrayList<>();
		for(OrderItem item : orderItems) {
			Product product = productRepo.findById(item.getProductId()).orElse(null);
			if(product == null) {
				continue;
			}
			
			List<ProductImage> images = productImageRepo.findByProduct_ProductId(product.getProductId());
			String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
			
			Map<String, Object> productDetails = new HashMap<>();
			productDetails.put("order_id", item.getOrder().getOrderId());
			productDetails.put("quantity", item.getQuantity());
			productDetails.put("total_price", item.getTotalPrice());
			productDetails.put("image_url", imageUrl);
			productDetails.put("product_id", product.getProductId());
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", item.getPricePerUnit());
			
			products.add(productDetails);
		}
		
		response.put("products", products);
		
		return response;
		
	}
}
