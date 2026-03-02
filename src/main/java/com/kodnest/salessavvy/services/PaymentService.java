package com.kodnest.salessavvy.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.CartItem;
import com.kodnest.salessavvy.entities.Order;
import com.kodnest.salessavvy.entities.OrderItem;
import com.kodnest.salessavvy.entities.OrderStatus;
import com.kodnest.salessavvy.repositories.CartRepository;
import com.kodnest.salessavvy.repositories.OrderItemRepository;
import com.kodnest.salessavvy.repositories.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	@Value("${razorpay.key_id}")
	private String razorpayKeyId;
	
	@Value("${razorpay.key_secret}")
	private String razorpayKeySecret;
	
	private OrderRepository orderRepo;
	
	private final OrderItemRepository orderItemRepo;
	
	
	private final CartRepository cartRepo;
	
	public PaymentService(OrderRepository orderRepo, OrderItemRepository orderItemRepo, CartRepository cartRepo) {
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
		this.cartRepo = cartRepo;
	}
	
	@Transactional
	public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException {
		RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
		
		var orderRequest = new JSONObject();
		orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue());
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
		
		com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
		
		Order order = new Order();
		order.setOrderId(razorpayOrder.get("id"));
		order.setUserId(userId);
		order.setTotalAmount(totalAmount);
		order.setStatus(OrderStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());
		orderRepo.save(order);
		
		return razorpayOrder.get("id");
	}
	
	@Transactional
	public boolean verifyPayment(String razorpayOrderId, String razorpayPayment, String razorpaySignature, int userId) {
		try {
			JSONObject attributes = new JSONObject();
			attributes.put("razorpay_order_id", razorpayOrderId);
			attributes.put("razorpay_payment_id", razorpayPayment);
			attributes.put("razorpay_signature", razorpaySignature);
			
			boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
			
			if(isSignatureValid) {
				Order order = orderRepo.findById(razorpayOrderId).orElseThrow(() -> new RuntimeException("Order not found"));
				order.setStatus(OrderStatus.SUCCESS);
				order.setUpdatedAt(LocalDateTime.now());
				orderRepo.save(order);
			
				List<CartItem> cartItems = cartRepo.findCartItemsWithProductDetails(userId);
				
				for(CartItem cartItem : cartItems) {
					OrderItem orderItem = new OrderItem();
					orderItem.setOrder(order);
					orderItem.setProductId(cartItem.getProduct().getProductId());
					orderItem.setQuantity(cartItem.getQuantity());
					orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
					
					orderItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
					
					orderItemRepo.save(orderItem);
					
				}
				
				cartRepo.deleteAllCartItemsByUserId(userId);
				return true;
			}
			
			else {
				return false;
			}
		} 
		catch (Exception e) {
				e.printStackTrace();
				return false;
		}
	}
	
	@Transactional
	public void saveOrderItems(String orderId, List<OrderItem> items) {
		Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		
		for(OrderItem item : items) {
			item.setOrder(order);
			orderItemRepo.save(item);
		}
	}
}
