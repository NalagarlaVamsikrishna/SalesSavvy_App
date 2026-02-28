package com.kodnest.salessavvy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entities.Category;
import com.kodnest.salessavvy.entities.Product;
import com.kodnest.salessavvy.entities.ProductImage;
import com.kodnest.salessavvy.repositories.CategoryRepository;
import com.kodnest.salessavvy.repositories.ProductImageRepository;
import com.kodnest.salessavvy.repositories.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductImageRepository productImageRepo;
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Product> getProductByCategory(String categoryName) {
		
		if(categoryName != null && categoryName.isEmpty()) {
			Optional<Category> categoryOpt = categoryRepo.findByCategoryName(categoryName);
			
			if(categoryOpt.isPresent()) {
				Category category = categoryOpt.get();
				return productRepo.findByCategory_CategoryId(category.getCategoryId());
			} 
			else {
				throw new RuntimeException("Category not found");
			}
		} 
		else {
			return productRepo.findAll();
		}
	}
	
	public List<String> getProductImages(Integer productId) {
		
		List<ProductImage> productImages = productImageRepo.findByProduct_ProductId(productId);
		List<String> imageUrls = new ArrayList<>();
		
		for(ProductImage image : productImages) {
			imageUrls.add(image.getImageUrl());
		}
		
		return imageUrls;
	}
}
