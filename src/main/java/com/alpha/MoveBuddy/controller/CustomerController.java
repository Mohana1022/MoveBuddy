package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.AvailableVehiclesDTO;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.service.CustomerService;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping("/registercustomer")
    public ResponseStructure<String> register(@RequestBody RegisterCustomerDTO dto) {
        return service.saveCustomer(dto);
    }

    @DeleteMapping("/deletecustomer")
    public ResponseEntity<ResponseStructure<String>> delete(@RequestParam long mobileNo) {
        return ResponseEntity.ok(service.deletecustomer(mobileNo));
    }

    @GetMapping("/findcustomer")
    public ResponseEntity<ResponseStructure<Customer>> find(@RequestParam long mobileNo) {
        return ResponseEntity.ok(service.findCustomer(mobileNo));
    }

    @GetMapping("/seeavailableVehicles")
    public ResponseStructure<AvailableVehiclesDTO> availableVehicles(
            @RequestParam long mobileNo,
            @RequestParam String destination) {

        return service.getAvailableVehicles(mobileNo, destination);
    }
}

