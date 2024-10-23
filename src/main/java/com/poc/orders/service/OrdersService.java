package com.poc.orders.service;

import com.poc.orders.entities.Orders;

import java.util.List;

public interface OrdersService {

    public Orders placeOrder(Orders orders);

    public List<Orders> getAllOrders();

    public Orders getOrderById(int orderid);

    public Orders updateOrderById(Orders orders, int orderid);

    public void deleteOrderById(int orderid);
}
