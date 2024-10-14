package com.example.elkprac.product.service;

import com.example.elkprac.common.exception.CustomException;
import com.example.elkprac.common.message.ErrorMessage;
import com.example.elkprac.product.dto.request.ProductRegisterRequestDto;
import com.example.elkprac.product.dto.response.ProductInfoResponseDto;
import com.example.elkprac.product.dto.response.ProductRegisterResponseDto;
import com.example.elkprac.product.entity.Product;
import com.example.elkprac.product.repository.ProductRepository;
import com.example.elkprac.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductRegisterResponseDto registerProduct(ProductRegisterRequestDto productRegisterRequestDto, User user) {
        List<String> connectionType = productRegisterRequestDto.connectionType();

        return productRepository.findByProductName(productRegisterRequestDto.productName())
                .map(this::updateProductStock) // 상품이 존재하면 재고 증가
                .orElseGet(() -> registerNewProduct(productRegisterRequestDto, user)); // 상품이 없으면 새로 등록
    }

    private ProductRegisterResponseDto updateProductStock(Product product) {
        product.increaseStock(1);
        log.info("기존 상품의 재고가 1 증가되었습니다: {}", product.getStockQuantity());
        return ProductRegisterResponseDto.from(product);
    }

    private ProductRegisterResponseDto registerNewProduct(ProductRegisterRequestDto requestDto, User user) {
        Product newProduct = Product.builder()
                .productName(requestDto.productName())
                .productNumber(requestDto.productNumber())
                .connectionType(requestDto.connectionType())
                .stockQuantity(1)
                .user(user)
                .build();
        productRepository.save(newProduct);
        log.info("새로운 상품이 등록되었습니다: {}", newProduct.getProductName());
        return ProductRegisterResponseDto.from(newProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductInfoResponseDto> fetchProductInfo(User user) {
        List<Product> products = productRepository.findAllByUser_Id(user.getId());
        return products.stream()
                .map(ProductInfoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductInfoResponseDto fetchProductInfoById(Long productId, User user) {
        Product product = productRepository.findByIdAndUser_Id(productId, user.getId()).orElseThrow(
                () -> new CustomException(ErrorMessage.NOT_FOUND_PRODUCT)
        );
        return ProductInfoResponseDto.from(product);
    }
}
