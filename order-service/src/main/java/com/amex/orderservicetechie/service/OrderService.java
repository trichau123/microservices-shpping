package com.amex.orderservicetechie.service;

import com.amex.orderservicetechie.dto.InventoryResponse;
import com.amex.orderservicetechie.dto.OrderLineItemsDto;
import com.amex.orderservicetechie.dto.OrderRequest;
import com.amex.orderservicetechie.event.OrderPlaceEvent;
import com.amex.orderservicetechie.model.Order;
import com.amex.orderservicetechie.model.OrderLineItems;
import com.amex.orderservicetechie.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String,OrderPlaceEvent> kafkaTemplate;

    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());
        log.info("Calling inventory service");
        Span inventoryServiceLookup= tracer.nextSpan().name("InventoryServiceLookup");

         try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
             //Call inventory service, and place order if product is in stock
             InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get().uri("http://inventory-service/api/inventory",
                             uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes)
                                     .build())
                     .retrieve()
                     .bodyToMono(InventoryResponse[].class)
                     .block();
             boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);

             if(allProductsInStock){
                 orderRepository.save(order);
                 kafkaTemplate.send("notificationTopic",
                         new OrderPlaceEvent(order.getOrderNumber()));
                 return "Order Place Success";
             }
             else{
                 throw new IllegalArgumentException("Product not in stock please try again");
             }
             //add block is synchronouse

         }finally {
             inventoryServiceLookup.end();
         }

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
