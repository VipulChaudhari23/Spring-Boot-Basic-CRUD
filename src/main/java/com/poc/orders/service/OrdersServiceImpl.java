package com.poc.orders.service;

import com.poc.orders.entities.Orders;
import com.poc.orders.exception.OrderNotFoundException;
import com.poc.orders.repository.OrdersRepositoty;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService{

    @Autowired
    private OrdersRepositoty ordersRepositoty;

    private static final String ORDER_NOT_FOUND_MESSAGE = "Order with ID %d not found."; // Define a format string

    @Override
    public Orders placeOrder(Orders orders) {
        // Check if the incoming order object is null
        if (orders == null) {
            // Log an error message and throw an exception if order details are null
            String errorMessage = "Order details cannot be null.";
            log.error(errorMessage);
            throw new OrderNotFoundException(errorMessage);
        }

        // Create a new Orders object to hold the order details
        Orders or = new Orders();
        or.setOrderid(orders.getOrderid());
        or.setProductname(orders.getProductname());
        or.setProductprice(orders.getProductprice());
        or.setOrderdate(orders.getOrderdate());
        or.setShippingaddress(orders.getShippingaddress());

        // Log the order details before saving
        log.info("Placing order: {}", or);

        // Save the order and return the saved entity
        Orders savedOrder = ordersRepositoty.save(or);

        // Log a message indicating that the order has been successfully saved
        log.info("Order placed successfully: {}", savedOrder);

        return savedOrder;
    }

    @Override
    public List<Orders> getAllOrders() {
        return ordersRepositoty.findAll();
    }

    @Override
    public Orders getOrderById(int ordersid) {
        Orders getData = ordersRepositoty.findByOrderid(ordersid);

        if (getData != null){
            return getData;
        } else {
            String errorMessage = String.format(ORDER_NOT_FOUND_MESSAGE, ordersid);
            throw new OrderNotFoundException(errorMessage); // Or return null
        }
    }

    @Override
    public Orders updateOrderById(Orders updatedOrder, int orderid) {
        Orders existingOrder = ordersRepositoty.findByOrderid(orderid);
        if (existingOrder != null){
            existingOrder.setProductname(updatedOrder.getProductname());
            existingOrder.setProductprice(updatedOrder.getProductprice());
            existingOrder.setOrderdate(updatedOrder.getOrderdate());
            existingOrder.setShippingaddress(updatedOrder.getShippingaddress());

            return ordersRepositoty.save(existingOrder);
        } else{
            String errorMessage = String.format(ORDER_NOT_FOUND_MESSAGE, orderid);
            throw new OrderNotFoundException(errorMessage);
        }
    }

    @Override
    @Transactional
    public void deleteOrderById(int orderid) {
        // check if the order exixt
        Orders existingOrder = ordersRepositoty.findByOrderid(orderid);
        if (existingOrder != null){
            ordersRepositoty.deleteByOrderid(orderid);
        } else {
            throw new OrderNotFoundException("Order with ID " + orderid + " not found.");
        }
    }
}
