package com.example.productservicetechie.repository;

import com.example.productservicetechie.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
