package com.kodnest.salessavvy.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.Order;
import com.kodnest.salessavvy.entities.OrderItem;
import com.kodnest.salessavvy.repositories.OrderItemRepository;
import com.kodnest.salessavvy.repositories.OrderRepository;
import com.kodnest.salessavvy.repositories.ProductRepository;

@Service
public class AdminBusinessService {

	private final OrderRepository orderRepo;
	private final OrderItemRepository orderItemRepo;
	private final ProductRepository productRepo;
	
	public AdminBusinessService(OrderRepository orderRepo, OrderItemRepository orderItemRepo, ProductRepository productRepo) {
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
		this.productRepo = productRepo;
	}
	
	public Map<String, Object> calculateMonthlyBusiness(int month, int year) {
		List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByMonthAndYear(month, year);
		return calculateBusinessMetrics(successfulOrders);
	}
	
	public Map<String, Object> calculateDailyBusiness(LocalDate date) {
		List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByDate(date);
		return calculateBusinessMetrics(successfulOrders);
	}
	
	public Map<String, Object> calculateYearlyBusiness(int year) {
		List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByYear(year);
		return calculateBusinessMetrics(successfulOrders);
	}
	public Map<String, Object> calculateOverallBusiness() {
		List<Order> successfulOrders = orderRepo.findAllByStatus("SUCCESS");
		BigDecimal totalBusiness = orderRepo.calculateOverallBusiness();
		Map<String, Object> response = calculateBusinessMetrics(successfulOrders);
		response.put("totalBusiness", totalBusiness.doubleValue());
		return response;
	}
	
	private Map<String, Object> calculateBusinessMetrics(List<Order> orders) {
		double totalRevenue = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();
		for(Order order : orders) {
			totalRevenue += order.getTotalAmount().doubleValue();
			List<OrderItem> items = orderItemRepo.findByOrderId(order.getOrderId());
			
			for(OrderItem item : items) {
				String categoryName = productRepo.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
			}
		}
		Map<String, Object> metrics = new HashMap<>();
		metrics.put("totalRevenue", totalRevenue);
		metrics.put("categorySales", categorySales);
		return metrics;
	}
}
