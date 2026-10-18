package com.orderflow.order_service.dto.responsedto;

public record OrderResponse(
    Long id,
    String productName,
    String status
) {
    
}
