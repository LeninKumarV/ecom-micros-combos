package com.example.product.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductsVo {

    private UUID productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigInteger stockQuantity;
    private String category;
    private String imageUrl;
    private Boolean isActive;
    private String createdOn;
    private String updatedOn;
    private String response;
}


