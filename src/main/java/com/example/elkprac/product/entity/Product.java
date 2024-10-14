package com.example.elkprac.product.entity;

import com.example.elkprac.common.util.StringListConverter;
import com.example.elkprac.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productNumber;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> connectionType = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Product(String productName, String productNumber, int stockQuantity, List<String> connectionType, User user) {
        this.productName = productName;
        this.productNumber = productNumber;
        this.stockQuantity = stockQuantity;
        this.connectionType = connectionType;
        this.user = user;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
