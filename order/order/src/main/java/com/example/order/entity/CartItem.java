package com.example.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cartItemId;

    @Column(name = "user_id")
    private String userId;
    private UUID products;

    private BigDecimal price;
    private BigInteger quantity;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
