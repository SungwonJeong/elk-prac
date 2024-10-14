package com.example.elkprac.product.dto.response;

import com.example.elkprac.product.entity.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductInfoResponseDto {

    private final Long productId;
    private final String productName;
    private final String productNumber;
    private final int stockQuantity;
    private final List<String> connectionType;

    private ProductInfoResponseDto(Product product) {
        this.productId = product.getId();
        this.productName = product.getProductName();
        this.productNumber = product.getProductNumber();
        this.stockQuantity = product.getStockQuantity();
        this.connectionType = product.getConnectionType();
    }

    public static ProductInfoResponseDto from(Product product) {
        return new ProductInfoResponseDto(product);
    }
}
