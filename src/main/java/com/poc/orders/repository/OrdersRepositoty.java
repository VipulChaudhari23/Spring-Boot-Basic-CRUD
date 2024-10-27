package com.poc.orders.repository;

import com.poc.orders.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface OrdersRepositoty extends JpaRepository<Orders, Integer> {
    Orders findByOrderid(int orderid);
    void deleteByOrderid(int orderid);

    // New method to find by orderid and productname
    Orders findByOrderidAndProductname(int orderid, String productname);
}
