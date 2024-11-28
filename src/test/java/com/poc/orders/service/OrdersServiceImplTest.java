package com.poc.orders.service;

import com.poc.orders.entities.Orders;
import com.poc.orders.exception.OrderNotFoundException;
import com.poc.orders.repository.OrdersRepositoty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdersServiceImplTest {

    @InjectMocks
    private OrdersServiceImpl ordersService;

    @Mock
    private OrdersRepositoty ordersRepositoty;

    private Orders order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Orders();
        order.setOrderid(1);
        order.setProductname("Product A");
        order.setProductprice(100.0);
        order.setOrderdate(Date.valueOf("2024-01-01"));
        order.setShippingaddress("Pune");
    }

    @Test
    void placeOrder_ShouldReturnPlacedOrder() {
        when(ordersRepositoty.save(order)).thenReturn(order);

        Orders placedOrder = ordersService.placeOrder(order);

        assertNotNull(placedOrder);
        assertEquals(order.getOrderid(), placedOrder.getOrderid());
        assertEquals(order.getProductname(), placedOrder.getProductname());
        assertEquals(order.getProductprice(), placedOrder.getProductprice());
        assertEquals(order.getShippingaddress(), placedOrder.getShippingaddress());
        verify(ordersRepositoty, times(1)).save(order);
    }

    @Test
    void placeOrder_ShouldThrowException_WhenOrderIsNull() {
        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            ordersService.placeOrder(null);
        });

        assertEquals("Order details cannot be null.", exception.getMessage());
    }

    @Test
    void getAllOrders_ShouldReturnOrderList() {
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(order);
        when(ordersRepositoty.findAll()).thenReturn(ordersList);

        List<Orders> result = ordersService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order.getProductname(), result.get(0).getProductname());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(ordersRepositoty.findByOrderid(1)).thenReturn(order);

        Orders result = ordersService.getOrderById(1);

        assertNotNull(result);
        assertEquals(order.getOrderid(), result.getOrderid());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(ordersRepositoty.findByOrderid(1)).thenReturn(null);

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            ordersService.getOrderById(1);
        });

        assertEquals("Order with ID 1 not found", exception.getMessage());
    }

    @Test
    void updateOrderById_ShouldReturnUpdatedOrder() {
        Orders updatedOrder = new Orders();
        updatedOrder.setOrderid(1);
        updatedOrder.setProductname("Product B");
        updatedOrder.setProductprice(150.0);
        updatedOrder.setOrderdate(Date.valueOf("2024-01-10"));
        updatedOrder.setShippingaddress("Mumbai");

        when(ordersRepositoty.findByOrderid(1)).thenReturn(order);
        when(ordersRepositoty.save(any(Orders.class))).thenReturn(updatedOrder);

        Orders result = ordersService.updateOrderById(updatedOrder, 1);

        assertNotNull(result);
        assertEquals("Product B", result.getProductname());
        assertEquals(150.0, result.getProductprice());
        assertEquals("Mumbai", result.getShippingaddress());
    }

    @Test
    void updateOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(ordersRepositoty.findByOrderid(1)).thenReturn(null);

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            ordersService.updateOrderById(order, 1);
        });

        assertEquals("Order with ID 1 not found", exception.getMessage());
    }

    @Test
    void deleteOrderById_ShouldCallDelete_WhenOrderExists() {
        when(ordersRepositoty.findByOrderid(1)).thenReturn(order);

        ordersService.deleteOrderById(1);

        verify(ordersRepositoty, times(1)).deleteByOrderid(1);
    }

    @Test
    void deleteOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(ordersRepositoty.findByOrderid(1)).thenReturn(null);

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            ordersService.deleteOrderById(1);
        });

        assertEquals("Order with ID 1 not found.", exception.getMessage());
    }
}
