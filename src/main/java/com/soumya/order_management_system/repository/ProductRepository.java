package com.soumya.order_management_system.repository;

import com.soumya.order_management_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}