package com.example.hanaro.repository;

import com.example.hanaro.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String keyword);
}
