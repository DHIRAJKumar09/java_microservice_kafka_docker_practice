package com.orderflow.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateStatusRequest {

    @NotBlank(message = "status is mandatory")
    @Pattern(
        regexp = "CREATED|PROCESSING|CONFIRMED|CANCELLED",
        message = "Status must be one of the following: CREATED, PROCESSING, COMPLETED, CANCELLED"
    )
    String status;

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    
}
