package com.kodnest.salessavvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kodnest.salessavvy.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query("SELECT p.category.categoryName FROM Product p WHERE p.productId = :productId")
	List<Product> findByCategory_CategoryId(Integer categoryId);
}
