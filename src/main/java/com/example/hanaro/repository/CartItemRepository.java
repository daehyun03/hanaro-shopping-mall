package com.example.hanaro.repository;

import com.example.hanaro.entity.Cart;
import com.example.hanaro.entity.CartItem;
import com.example.hanaro.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
