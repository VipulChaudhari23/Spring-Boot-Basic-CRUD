package com.poc.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.orders.entities.Orders;
import com.poc.orders.exception.OrderNotFoundException;
import com.poc.orders.service.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrdersService ordersService;

    @InjectMocks
    private OrdersController ordersController;

    private Orders order;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        // Initialize MockMvc with the OrdersController
        mockMvc = MockMvcBuilders.standaloneSetup(ordersController).build();

        order = new Orders();
        order.setOrderid(1);
        order.setProductname("Mobile Phone");
        order.setProductprice(25999.9);
        order.setOrderdate(Date.valueOf("2024-10-25"));
        order.setShippingaddress("Pune");
    }

    @Test
    public void placeOrder_ShouldReturnCreatedOrder() throws Exception {
        // Create a sample order request body
        Orders order = new Orders();
        order.setProductname("Sample Product");
        order.setProductprice(99.99);

        // Mock the service to return the order when the placeOrder method is called
        when(ordersService.placeOrder(any(Orders.class))).thenReturn(order);

        // Convert the order object to JSON
        String orderJson = new ObjectMapper().writeValueAsString(order);

        mockMvc.perform(post("/orders/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())  // Expect 200 OK as per your controller
                .andExpect(jsonPath("$.productname").value("Sample Product"))  // Check for the product name in response
                .andExpect(jsonPath("$.productprice").value(99.99));  // Check for product price
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(ordersService.getOrderById(1)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productname").value("Mobile Phone"))
                .andExpect(jsonPath("$.shippingaddress").value("Pune"));
    }

    @Test
    void getOrderById_ShouldReturnNotFound_WhenOrderNotExists() throws Exception {
        when(ordersService.getOrderById(1)).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with ID 1 not found."))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getAllOrders_ShouldReturnOrderList() throws Exception {
        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(order);
        when(ordersService.getAllOrders()).thenReturn(ordersList);

        mockMvc.perform(get("/orders/allOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productname").value("Mobile Phone"));
    }

    @Test
    void getAllOrders_ShouldReturnNotFound_WhenEmptyList() throws Exception {
        when(ordersService.getAllOrders()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/orders/allOrders"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders data found."));
    }

    @Test
    void updateOrderById_ShouldReturnUpdatedOrder() throws Exception {
        // Mock the service to return the updated order
        when(ordersService.updateOrderById(any(Orders.class), anyInt())).thenReturn(order);

        mockMvc.perform(put("/orders/updateOrder/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productname\":\"Mobile Phone\",\"productprice\":150000,\"orderdate\":\"2024-10-09\",\"shippingaddress\":\"Mumbai\"}"))
                .andDo(print())  // Print the response
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$.productname").value("Mobile Phone"))
                .andExpect(jsonPath("$.productprice").value(25999))
                .andExpect(jsonPath("$.shippingaddress").value("Pune"));
    }

    @Test
    void deleteOrder_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/orders/deleteOrder/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order with ID 1 deleted successfully."));
    }
}
