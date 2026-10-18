package com.orderflow.order_service.service;
import com.orderflow.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.orderflow.order_service.entity.Order;
@Service 
public class OrderService {

    public final OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    //create order method
    public Order createOrder(String productName){
       Order order =    new Order();
       order.setProductName(productName);
       order.setStatus("CREATED");
       return orderRepository.save(order);
    }
    // get status method
    public String getStatus(){
        return "your order is in progress";
    }
    // GET order by id method
    public Order getOrderById(Long id){
        return orderRepository.findById(id)
        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"product is not found"));
    }
    // UPDATE order status method
    @Transactional 
    public Order updateOrderStatus(Long id , String status){
        Order order = getOrderById(id);
        if("CANCELLED".equals(order.getStatus()) && !"CANCELLED".equals(status)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot change this status from CANCELLED TO " + status);
        }
        order.setStatus(status);
        return order;
    }


 
    
}
