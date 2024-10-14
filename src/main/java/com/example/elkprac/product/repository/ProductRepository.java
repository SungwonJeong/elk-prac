package com.example.elkprac.product.repository;

import com.example.elkprac.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);

    List<Product> findAllByUser_Id(Long userId);

    Optional<Product> findByIdAndUser_Id(Long productId, Long userId);
}
