package com.example.order.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemVo {

    private UUID cartItemId;
    private String userId;
    private UUID productId;
    private UUID user;
    private UUID products;
    private BigDecimal price;
    private BigInteger quantity;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String response;
}

