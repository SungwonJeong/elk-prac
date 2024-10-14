package com.example.elkprac.product.controller;

import com.example.elkprac.common.dto.ResponseMessageDto;
import com.example.elkprac.common.message.ResponseMessage;
import com.example.elkprac.product.dto.request.ProductRegisterRequestDto;
import com.example.elkprac.product.dto.response.ProductInfoResponseDto;
import com.example.elkprac.product.dto.response.ProductRegisterResponseDto;
import com.example.elkprac.product.service.ProductService;
import com.example.elkprac.security.util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseMessageDto<ProductRegisterResponseDto>> registerProduct(@RequestBody ProductRegisterRequestDto productRegisterRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductRegisterResponseDto response = productService.registerProduct(productRegisterRequestDto, userDetails.getUser());
        return ResponseMessageDto.toResponseEntity(ResponseMessage.PRODUCT_REGISTER_SUCCESS, response);
    }

    @GetMapping
    public ResponseEntity<ResponseMessageDto<List<ProductInfoResponseDto>>> fetchProductInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductInfoResponseDto> response = productService.fetchProductInfo(userDetails.getUser());
        return ResponseMessageDto.toResponseEntity(ResponseMessage.PRODUCT_FETCH_SUCCESS, response);
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ResponseMessageDto<ProductInfoResponseDto>> fetchProductInfoById(@PathVariable("product_id") Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductInfoResponseDto response = productService.fetchProductInfoById(productId, userDetails.getUser());
        return ResponseMessageDto.toResponseEntity(ResponseMessage.PRODUCT_FETCH_SUCCESS, response);
    }
}
