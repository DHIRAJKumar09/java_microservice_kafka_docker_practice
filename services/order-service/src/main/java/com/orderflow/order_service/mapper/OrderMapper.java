package com.orderflow.order_service.mapper;

import com.orderflow.order_service.dto.responsedto.OrderResponse;
import com.orderflow.order_service.entity.Order;

public class OrderMapper {
    public static OrderResponse toResponse(Order order){
        return new OrderResponse(
            order.getId(),
            order.getProductName(),
            order.getStatus()
        );
    }
}
