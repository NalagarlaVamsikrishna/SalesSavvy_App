package com.kodnest.salessavvy.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.Category;
import com.kodnest.salessavvy.entities.Product;
import com.kodnest.salessavvy.entities.ProductImage;
import com.kodnest.salessavvy.repositories.CategoryRepository;
import com.kodnest.salessavvy.repositories.ProductImageRepository;
import com.kodnest.salessavvy.repositories.ProductRepository;

@Service
public class AdminProductService {
	
	private final ProductRepository productRepo;
	private final ProductImageRepository productImageRepo;
	private final CategoryRepository categoryRepo;
	
	public AdminProductService(ProductRepository productRepo, ProductImageRepository productImageRepo, CategoryRepository categoryRepo) {
		this.productRepo = productRepo;
		this.productImageRepo = productImageRepo;
		this.categoryRepo = categoryRepo;
	}
	
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl) {
		Optional<Category> category = categoryRepo.findById(categoryId);
		if(category.isEmpty()) {
			throw new IllegalArgumentException("Invalid category ID");
		}
		
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setPrice(BigDecimal.valueOf(price));
		product.setStock(stock);
		product.setCategory(category.get());
		product.setCreatedAt(LocalDateTime.now());
		product.setUpdatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepo.save(product);
		
		if(imageUrl != null && !imageUrl.isEmpty()) {
			ProductImage productImage = new ProductImage();
			productImage.setProduct(savedProduct);
			productImage.setImageUrl(imageUrl);
			productImageRepo.save(productImage);			
		}
		else {
			throw new IllegalArgumentException("Product image URL cannot be empty");
		}
		return savedProduct;
	}
	
	public void deleteProduct(Integer productId) {
		if(!productRepo.existsById(productId)) {
			throw new IllegalArgumentException("Product not found");
		}
		productImageRepo.deleteById(productId);
	}
}
