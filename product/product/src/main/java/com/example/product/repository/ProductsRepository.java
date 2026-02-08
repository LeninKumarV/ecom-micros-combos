package com.example.product.repository;

import com.example.product.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<Products, UUID> {

    List<Products> findByIsActiveTrue();

    @Query("SELECT p FROM Products p WHERE LOWER(REPLACE(p.productName, ' ', '')) " +
            "LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) AND p.isActive = true")
    List<Products> searchIgnoreSpaces(@Param("keyword") String keyword);

}
