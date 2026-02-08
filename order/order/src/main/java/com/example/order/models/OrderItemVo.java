package com.example.order.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class OrderItemVo {

        private UUID orderItemId;
        @JsonIgnore
        private OrderVo order;
        private ProductsVo products;
        private BigDecimal price;
        private BigInteger quantity;
        private LocalDateTime createdOn;
        private LocalDateTime updatedOn;

}
