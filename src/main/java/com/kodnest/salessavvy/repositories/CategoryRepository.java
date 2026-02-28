package com.kodnest.salessavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodnest.salessavvy.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Optional<Category> findByCategoryName(String categoryName);

	
}
