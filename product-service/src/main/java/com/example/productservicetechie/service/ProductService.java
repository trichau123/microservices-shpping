package com.example.productservicetechie.service;

import com.example.productservicetechie.dto.ProductRequest;
import com.example.productservicetechie.dto.ProductResponse;
import com.example.productservicetechie.model.Product;
import com.example.productservicetechie.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder().
                name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        String id = productRepository.save(product).getId();
        log.info("Product id {} is saved",id);
    }

    public List<ProductResponse> getAllProducts() {
       List<Product> products= productRepository.findAll();
        List<ProductResponse> collect = products.stream()
                .map(this::mapToProductResponse).collect(Collectors.toList());
        return collect;

    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
