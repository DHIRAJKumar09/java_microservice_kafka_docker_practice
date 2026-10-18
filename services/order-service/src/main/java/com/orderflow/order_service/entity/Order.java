package com.orderflow.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity 
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product Name is required")
    private String productName;

    private String status;

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
    public Long getId(){
        return id;
    }
    public String getProductName(){
        return productName;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    

}
