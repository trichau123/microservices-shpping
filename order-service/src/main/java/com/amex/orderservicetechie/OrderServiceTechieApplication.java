package com.amex.orderservicetechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class OrderServiceTechieApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceTechieApplication.class, args);
	}

}
