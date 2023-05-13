package com.amex.orderservicetechie.repository;

import com.amex.orderservicetechie.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
