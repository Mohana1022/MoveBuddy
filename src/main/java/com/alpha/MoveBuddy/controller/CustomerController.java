package com.alpha.MoveBuddy.controller;

import java.util.List;

import org.hibernate.annotations.ConcreteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Vehicle;
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
	
	@GetMapping("/findcustomer")
	public ResponseEntity<ResponseStructure<Customer>> findCustomer(@RequestParam long customerMobileno) {
	    ResponseStructure<Customer> response = cs.findCustomer(customerMobileno);
	    return ResponseEntity.ok(response);
	}
	
	 @GetMapping("/seeAvailableVehicles")
	    public ResponseEntity<ResponseStructure<List<Vehicle>>> seeAvailableVehicles(@RequestParam long mobileNo) {
	        ResponseStructure<List<Vehicle>> rs = cs.getAvailableVehicles(mobileNo);
	        return new ResponseEntity<>(rs, HttpStatus.valueOf(rs.getStatuscode()));
	    }

}
