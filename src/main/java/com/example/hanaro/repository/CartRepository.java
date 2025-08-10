package com.example.hanaro.repository;

import com.example.hanaro.entity.Cart;
import com.example.hanaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}