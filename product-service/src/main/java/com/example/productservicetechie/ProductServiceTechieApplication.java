package com.example.productservicetechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceTechieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceTechieApplication.class, args);
    }

}
