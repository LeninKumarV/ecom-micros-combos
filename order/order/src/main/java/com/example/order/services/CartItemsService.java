package com.example.order.services;

import com.example.common_contracts.models.UserCommonVo;
import com.example.order.clients.ProductClient;
import com.example.order.clients.UserClient;
import com.example.order.conifg.RabbitMQConfig;
import com.example.order.models.CartItemVo;
import com.example.order.entity.CartItem;
import com.example.order.models.ProductsVo;
import com.example.order.models.UserVo;
import com.example.order.respository.CartItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemsService {

//    private final ProductsRepository productsRepository;
//    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ObjectMapper mapper;
    private final ProductClient productClient;
    private final UserClient  userClient;
    private final RabbitTemplate  rabbitTemplate;


    @Transactional
    public CartItemVo saveCartItem(CartItemVo vo) {

        final UserCommonVo user;
        try {
             user = Optional.ofNullable(
                    (UserCommonVo) rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.EXCHANGE,
                            RabbitMQConfig.USER_ROUTING_KEY,
                            vo.getUserId()
                    )
            ).orElseThrow(() -> new RuntimeException("User not found"));


//            // FIXED: Convert LinkedHashMap to UserVo using ObjectMapper
//            if (response instanceof LinkedHashMap) {
//                user = mapper.convertValue(response, UserVo.class);
//            } else {
//                user = (UserVo) response;
//            }
            log.info("Successfully fetched user: {}", user.getUserId());

        } catch (Exception e) {
            log.error("Failed to fetch user", e);
            throw new RuntimeException("User validation failed", e);
        }

        System.out.println("UserVO: " + user);

//        UserVo user = userClient.getUserById(vo.getUserId());
        ProductsVo product = productClient.getProductById(vo.getProductId());

        if(product == null){
            return CartItemVo.builder()
                    .response("Product or User is not found")
                    .build();
        }

        if (product.getStockQuantity().compareTo(vo.getQuantity()) < 0) {
            return CartItemVo.builder()
                    .response("Selected product stock is not available")
                    .build();
        }

        return cartItemRepository
                .findCartItemByUserAndProduct(
                        user.getUserId(),
                        vo.getProductId()
                )
                .map(cartItem -> {

                    // update quantity
                    BigInteger updatedQty =
                            cartItem.getQuantity().add(vo.getQuantity());
                    cartItem.setQuantity(updatedQty);

                    // update total price
                    BigDecimal additionalPrice = product.getPrice().multiply(new BigDecimal(vo.getQuantity()));
                    cartItem.setPrice(cartItem.getPrice().add(additionalPrice));

                    cartItem.setUpdatedOn(LocalDateTime.now());

                    CartItem saved = cartItemRepository.save(cartItem);
                    return mapToVo(saved, "Cart Item Updated Successfully");
                })
                .orElseGet(() -> {
                    CartItem cartItem = CartItem.builder()
                            .cartItemId(vo.getCartItemId())
                            .userId(user.getUserId())
                            .products(product.getProductId())
                            .price(
                                    product.getPrice()
                                            .multiply(new BigDecimal(vo.getQuantity()))
                            )
                            .quantity(vo.getQuantity())
                            .createdOn(LocalDateTime.now())
                            .updatedOn(LocalDateTime.now())
                            .build();

                    CartItem saved = cartItemRepository.save(cartItem);
                    return mapToVo(saved, "Cart Item Saved Successfully");
                });


    }

    @Transactional
    public CartItemVo deleteCartItem(CartItemVo vo) {
        System.out.println("userId = " + vo.getUserId() +" "+ "productId =  " + vo.getProductId());
        int deletedCount = cartItemRepository
                .deleteCartItemByUserAndProduct(
                        vo.getUserId(),
                        vo.getProductId()
                );
        if (deletedCount == 0) {
            throw new IllegalStateException("Cart item not found!");
        }
        return CartItemVo.builder()
                .userId(vo.getUserId())
                .productId(vo.getProductId())
                .response("Cart item deleted successfully")
                .build();
    }


    private CartItemVo mapToVo(CartItem cartItem, String response) {
        return CartItemVo.builder()
                .cartItemId(cartItem.getCartItemId())
                .userId(cartItem.getUserId())
                .products(cartItem.getProducts())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .response(response)
                .createdOn(cartItem.getCreatedOn())
                .updatedOn(cartItem.getUpdatedOn())
                .build();
    }

    public List<CartItemVo> getAllCartItem(String userId) {
        List<CartItem> cartItem = cartItemRepository.findAllCartItemsByUser(userId);
        return cartItem.stream().map( m -> mapToVo(m, null)).collect(Collectors.toList());
    }
}
