package com.kodnest.salessavvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.salessavvy.entities.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
	List<ProductImage> findByProduct_ProductId(Integer productId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
	void deleteByProductId(Integer productId);
	
	

}
