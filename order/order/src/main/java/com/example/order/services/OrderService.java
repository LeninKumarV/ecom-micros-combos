package com.example.order.services;

import com.example.order.clients.ProductClient;
import com.example.order.clients.UserClient;
import com.example.order.models.OrderItemVo;
import com.example.order.entity.OrderStatus;
import com.example.order.models.OrderVo;
import com.example.order.entity.CartItem;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.models.ProductsVo;
import com.example.order.models.UserVo;
import com.example.order.respository.CartItemRepository;
import com.example.order.respository.OrderItemRepository;
import com.example.order.respository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper mapper;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE = "app.topic.exchange";
    public static final String ROUTING_KEY = "user.created";

    @Transactional
    public OrderVo saveOrder(String userId) {
        UserVo user = userClient.getUserById(userId);

        if(user==null){
            return OrderVo.builder().response("User Not Found").build();
        }

        List<CartItem> cartItems = cartItemRepository.findAllCartItemsByUser(userId);
        if(cartItems.isEmpty()){
            OrderVo.builder().response("You have nothing to order on your cart. Please add something.").build();
        }
        Order order = Order.builder()
                .userId(user.getUserId())
                .orderStatus(OrderStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem item = mapToOrderItemsVo(cartItem);
                    item.setOrder(order);
                    return item;
                })
                .toList();

        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalPrice(orderItems));

        Order orderResponse = orderRepository.save(order);

        if (orderResponse.getOrderId() == null) {
            return OrderVo.builder()
                    .response("Something went wrong while placing the order")
                    .build();
        }

        cartItemRepository.deleteCartItemByUser(userId);
        productClient.updateProductPrice(getProductsById(orderItems));

        return  mapToOrderVo(order, user);
    }

    private OrderItem mapToOrderItemsVo(CartItem cartItem) {
        return OrderItem.builder()
                .products(cartItem.getProducts())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .createdOn(cartItem.getCreatedOn())
                .updatedOn(cartItem.getUpdatedOn())
                .build();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return
                orderItems.stream()
                        .map(OrderItem::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderVo mapToOrderVo(Order order, UserVo user) {
        return OrderVo.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .createdOn(order.getCreatedOn())
                .updatedOn(order.getUpdatedOn())
                .user(user)
                .orderItems(
                        order.getOrderItems().stream()
                                .map(item -> OrderItemVo.builder()
                                        .orderItemId(item.getOrderItemId())
                                        .price(item.getPrice())
                                        .quantity(item.getQuantity())
                                        .createdOn(item.getCreatedOn())
                                        .updatedOn(item.getUpdatedOn())
                                        .products(productClient.getProductById(item.getProducts()))
                                        .build())
                                .toList()
                )
                .createdOn(order.getCreatedOn())
                .updatedOn(order.getUpdatedOn())
                .response("Your order has been approved")
                .build();
    }

    private List<ProductsVo> getProductsById(List<OrderItem> orderItems) {

        return orderItems.stream()
                .map(item -> ProductsVo.builder()
                        .productId(item.getProducts())
                        .stockQuantity(item.getQuantity())
                        .build())
                .toList();
    }

    @Transactional
    public String getAMQPConfig(String config) {
        try{
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, config);
        }
        catch (Exception e){
            log.info("AMQP config could not be sent", e);
        }
        return config;
    }
}
