package com.kodnest.salessavvy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodnest.salessavvy.entities.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

}
