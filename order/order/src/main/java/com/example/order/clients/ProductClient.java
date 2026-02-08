package com.example.order.clients;

import com.example.order.models.ProductsVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product")
public interface ProductClient {

    @GetMapping("/api/products/get/{product-id}")
    ProductsVo getProductById(@PathVariable("product-id") UUID productId);

    @PostMapping("/api/products/update/quantity")
    void updateProductPrice(@RequestBody List<ProductsVo> productVO);

}
