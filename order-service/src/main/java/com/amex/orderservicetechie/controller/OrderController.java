package com.amex.orderservicetechie.controller;

import com.amex.orderservicetechie.dto.OrderRequest;
import com.amex.orderservicetechie.model.Order;
import com.amex.orderservicetechie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Order> getOrder(){
        return orderService.getOrders();
    }
}
