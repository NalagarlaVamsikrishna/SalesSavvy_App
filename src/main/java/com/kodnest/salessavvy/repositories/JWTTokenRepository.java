package com.kodnest.salessavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kodnest.salessavvy.entities.JWTToken;

import jakarta.transaction.Transactional;

public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {
	
	@Query("SELECT t FROM JWTToken t WHERE t.user.userId = :userId")
	JWTToken findByUserId(@Param("userId") int userId);

	Optional<JWTToken> findByToken(String token);
	
	@Modifying
	@Transactional
	@Query("DELETE JWTToken t WHERE t.user.userId = :userId")
	void deleteByUserId(@Param("userId") int userId);
	
}
