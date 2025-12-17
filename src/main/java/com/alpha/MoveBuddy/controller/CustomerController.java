package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.AvailableVehiclesDTO;
import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.service.CustomerService;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerservice;

    @PostMapping("/registercustomer")
    public ResponseStructure<String> register(@RequestBody RegisterCustomerDTO dto) {
        return customerservice.saveCustomer(dto);
    }

    @DeleteMapping("/deletecustomer")
    public ResponseEntity<ResponseStructure<String>> delete(@RequestParam long mobileNo) {
        return ResponseEntity.ok(customerservice.deletecustomer(mobileNo));
    }

    @GetMapping("/findcustomer")
    public ResponseEntity<ResponseStructure<Customer>> find(@RequestParam long mobileNo) {
        return ResponseEntity.ok(customerservice.findCustomer(mobileNo));
    }

    @GetMapping("/seeavailableVehicles")
    public ResponseStructure<AvailableVehiclesDTO> availableVehicles(@RequestParam long mobileNo,@RequestParam String destination) {

        return customerservice.getAvailableVehicles(mobileNo, destination);
    }
    
    @GetMapping("/seeactivebookings")
    public ResponseStructure<Customer> CustomerSeeActiveBooking(@RequestParam long mobileno)
    {
    	return customerservice.findCustomer(mobileno);
    }
    
    @GetMapping("/servicemethod")
    public ResponseStructure<Customer> servicemethod(@RequestParam long mobileNo) {
    	return customerservice.findCustomer(mobileNo);
    }
    
    @GetMapping("/seecustomerbookinghistory")
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeCustomerBookingHistory(@RequestParam long mobileNo) {
    	return customerservice.seeCustomerBookingHistory(mobileNo);
    }
    
    @PutMapping("/customerCancellation")
    public ResponseStructure<Customer> customerCancellation(int bookingid, int customerid) {
    	return customerservice.customerCancellation(bookingid, customerid);
    }
}

