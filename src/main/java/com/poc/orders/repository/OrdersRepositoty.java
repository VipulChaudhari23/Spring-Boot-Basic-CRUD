package com.poc.orders.repository;

import com.poc.orders.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepositoty extends JpaRepository<Orders, Integer> {
    Orders findByOrderid(int orderid);
    void deleteByOrderid(int orderid);
}
