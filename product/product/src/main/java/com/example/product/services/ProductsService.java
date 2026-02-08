package com.example.product.services;

import com.example.product.entity.Products;
import com.example.product.models.ProductsVo;
import com.example.product.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsService {

    private final ProductsRepository productsRepository;
    private static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-" +
                    "[0-9a-fA-F]{4}-" +
                    "[1-5][0-9a-fA-F]{3}-" +
                    "[89abAB][0-9a-fA-F]{3}-" +
                    "[0-9a-fA-F]{12}$";


    @Transactional
    public ProductsVo createAndUpdateProducts(ProductsVo vo) {

        if (vo.getProductId() != null && vo.getProductId().toString().matches(UUID_REGEX)) {
            log.info("Updating product with id {}", vo.getProductId());
            Products product = productsRepository.findById(vo.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Products updatedProduct = product.toBuilder()
                    .productName(vo.getProductName())
                    .description(vo.getDescription())
                    .price(vo.getPrice())
                    .stockQuantity(vo.getStockQuantity())
                    .category(vo.getCategory())
                    .imageUrl(vo.getImageUrl())
                    .isActive(vo.getIsActive())
                    .updatedOn(LocalDateTime.now())
                    .build();

            Products saved = productsRepository.save(updatedProduct);
            return buildVo(saved, "Product Updated Successfully");

        } else if(vo.getProductId() == null){
            Products product = Products.builder()
                    .productName(vo.getProductName())
                    .description(vo.getDescription())
                    .price(vo.getPrice())
                    .stockQuantity(vo.getStockQuantity())
                    .category(vo.getCategory())
                    .imageUrl(vo.getImageUrl())
                    .isActive(Boolean.TRUE)
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .build();

            Products saved = productsRepository.save(product);
            return buildVo(saved, "Product Saved Successfully");
        }
        return  ProductsVo.builder().response("Something Went Wrong").build();
    }

    private ProductsVo buildVo(Products product, String response) {

        return ProductsVo.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .createdOn(product.getCreatedOn().toString())
                .updatedOn(product.getUpdatedOn().toString())
                .response(response)
                .build();
    }

    public List<ProductsVo> getAllActive() {
        return productsRepository.findByIsActiveTrue()
                .stream()
                .map(product -> buildVo(product, null))
                .toList();
    }

    @Transactional
    public ProductsVo deleteProduct(UUID productId) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(false);
        product.setUpdatedOn(LocalDateTime.now());
        Products deleteProduct= productsRepository.save(product);
        return buildVo(deleteProduct, "Product Deleted Successfully");
    }


    public List<ProductsVo> searchProduct(String keyword) {
        List<Products> products =
                productsRepository.searchIgnoreSpaces(keyword);

        return products.stream()
                .map(product -> buildVo(product, null))
                .toList();
    }

    public ProductsVo getProductsById(UUID productId) {
        Optional<Products> products = productsRepository.findById(productId);
       return products
                .map(product -> buildVo(product, "Product Found Successfully"))
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void updateProductsQuantity(List<ProductsVo> vo) {
        log.info("Updating product quantity {}", vo);
        for (ProductsVo productsVo : vo) {
            Optional<Products> products = productsRepository.findById(productsVo.getProductId());
            products.ifPresent(product -> {
                BigInteger currentQty =
                        Optional.ofNullable(product.getStockQuantity()).orElse(BigInteger.ZERO);

                BigInteger deductQty =
                        Optional.ofNullable(productsVo.getStockQuantity()).orElse(BigInteger.ZERO);

                product.setStockQuantity(currentQty.subtract(deductQty));
                productsRepository.save(product);
            });
        }
    }

}



