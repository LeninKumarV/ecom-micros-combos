package com.example.order.models;

import com.example.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderVo {

    private UUID orderId;
    private UserVo user;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    @JsonManagedReference
    private List<OrderItemVo> orderItems;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String response;
}
