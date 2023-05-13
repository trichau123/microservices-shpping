package com.amex.orderservicetechie.service;

import com.amex.orderservicetechie.dto.OrderLineItemsDto;
import com.amex.orderservicetechie.dto.OrderRequest;
import com.amex.orderservicetechie.model.Order;
import com.amex.orderservicetechie.model.OrderLineItems;
import com.amex.orderservicetechie.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());
        //Call inventory service, and place order if product is in stock
        Boolean result = webClient.get().uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes)
                                .build())
                        .retrieve()
                                .bodyToMono(Boolean.class)
                                        .block();
        if(result){
            orderRepository.save(order);
        }
        else{
            throw new IllegalArgumentException("Product not in stock please try again");
        }
        //add block is synchronouse

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }

    public List<Order> getOrders() {
       return orderRepository.findAll();
    }
}
