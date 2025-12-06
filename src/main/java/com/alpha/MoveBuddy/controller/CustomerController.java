package com.alpha.MoveBuddy.controller;

import org.hibernate.annotations.ConcreteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.service.CustomerService;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService cs;
	

	@PostMapping("/registercustomer")
	public ResponseStructure<String> saveCustomer(@RequestBody RegisterCustomerDTO dto) {
	    return cs.saveCustomer(dto);
	}

	@DeleteMapping("/deletecustomer")
	public ResponseEntity<ResponseStructure<String>> deletecustomer(@RequestParam long mobileNo) {
		ResponseStructure<String> response = cs.deletecustomer(mobileNo);
        return ResponseEntity.ok(response);
	}
}
