package com.alpha.MoveBuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpha.MoveBuddy.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseStructure<String> handleDriverNotFound(DriverNotFoundException ex) {
        ResponseStructure<String> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage("Driver not found");
        response.setData(ex.getMessage());
        return response;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseStructure<String> handleCustomerNotFound(CustomerNotFoundException ex) {
        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.NOT_FOUND.value());
        rs.setMessage("Customer with given mobile number not found");
        rs.setData(ex.getMessage());
        return rs;
    }
}
