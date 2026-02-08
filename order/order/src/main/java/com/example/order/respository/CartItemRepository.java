package com.example.order.respository;

import com.example.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("""
           SELECT c
           FROM CartItem c
           WHERE c.userId = :userId
             AND c.products = :productId
           """)
    Optional<CartItem> findCartItemByUserAndProduct(
            @Param("userId") String userId,
            @Param("productId") UUID productId
    );

    @Query("""
           SELECT c
           FROM CartItem c
           WHERE c.userId = :userId
           """)
    List<CartItem> findAllCartItemsByUser(
            @Param("userId") String userId
    );


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
       DELETE
       FROM CartItem c
       WHERE c.userId = :userId
         AND c.products = :productId
       """)
    int deleteCartItemByUserAndProduct(
            @Param("userId") String userId,
            @Param("productId") UUID productId
    );


    @Modifying
    @Transactional
    @Query("""
           DELETE
           FROM CartItem c
           WHERE c.userId = :userId
           """)
    int deleteCartItemByUser(
            @Param("userId") String userId
    );
}


