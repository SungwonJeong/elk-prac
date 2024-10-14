package com.example.elkprac.product.dto.request;

import java.util.List;

public record ProductRegisterRequestDto(
        String productName,
        String productNumber,
        List<String> connectionType
) {
}
