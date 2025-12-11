package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.service.BookingService;

@RestController
public class BookingController {
	@Autowired
	private BookingService bookingservice;
	

	@PostMapping("/bookVehicle")
	public ResponseEntity<ResponseStructure<Booking>> bookVehicle(@RequestParam Long mobileNo, @RequestBody BookingDTO bookingdto) {
		
		ResponseStructure<Booking> response = bookingservice.bookVehicle(mobileNo, bookingdto);
	    return ResponseEntity.ok(response);
	}
	
}
