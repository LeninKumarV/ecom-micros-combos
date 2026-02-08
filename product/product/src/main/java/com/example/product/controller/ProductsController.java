package com.example.product.controller;

import com.example.product.models.ProductsVo;
import com.example.product.services.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    @PostMapping("/save")
    public ResponseEntity<ProductsVo> createProduct(@RequestBody ProductsVo productVO) {
        return new ResponseEntity<>(productsService.createAndUpdateProducts(productVO), HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductsVo>> getAllActive() {
        return ResponseEntity.ok(productsService.getAllActive());
    }

    @GetMapping("delete/{productId}")
    public ResponseEntity<ProductsVo> delete(@PathVariable UUID productId) {
        return new ResponseEntity<>(productsService.deleteProduct(productId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductsVo>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(productsService.searchProduct(keyword));
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductsVo> getProductbyId(@PathVariable UUID productId) {
        return ResponseEntity.ok(productsService.getProductsById(productId));
    }

    @PostMapping("/update/quantity")
    public ResponseEntity<Void> updateQuantityOfProduct(@RequestBody List<ProductsVo> productVO) {
        productsService.updateProductsQuantity(productVO);
        return ResponseEntity.noContent().build();
    }

}


