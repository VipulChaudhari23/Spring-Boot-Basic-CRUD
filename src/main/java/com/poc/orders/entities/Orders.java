package com.poc.orders.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @Column(name = "Order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderid;

    @Column(name = "Product_name")
    private String  productname;

    @Column(name = "Product_price")
    private double productprice;

    @Column(name = "Order_date")
    private Date orderdate;

    @Column(name = "Shipping_address")
    private String shippingaddress;

}
