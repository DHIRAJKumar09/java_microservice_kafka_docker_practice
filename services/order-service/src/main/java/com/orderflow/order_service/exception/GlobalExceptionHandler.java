package com.orderflow.order_service.exception;
import java.util.Map;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.orderflow.order_service.exception.OrderNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.LinkedHashMap;
@RestControllerAdvice 
public class GlobalExceptionHandler {
    @ExceptionHandler (OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public  Map<String,String> handleOrderNotFound( OrderNotFoundException exception){
          return Map.of("message", exception.getMessage());
          
    }
    public ResponseEntity<Map<String,String>> handleValidation(
        MethodArgumentNotValidException exception){
            Map<String,String> errors = new LinkedHashMap<>();
            exception.getBindingResult().getFieldErrors()
            .forEach(error->errors.put(
                error.getField(),
                error.getDefaultMessage())
            );
        return ResponseEntity.badRequest().body(errors);
    }
    
}
