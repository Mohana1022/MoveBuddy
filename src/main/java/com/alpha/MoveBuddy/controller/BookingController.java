package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.service.BookingService;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //  BOOK VEHICLE
    @PostMapping("/bookVehicle")
    public ResponseEntity<ResponseStructure<Booking>> bookVehicle(
            @RequestParam Long mobileNo,
            @RequestBody BookingDTO bookingdto) {

        return ResponseEntity.ok(
                bookingService.bookVehicle(mobileNo, bookingdto)
        );
    }

    //  COMPLETE RIDE (OTP VERIFY)
    @PostMapping("/completeRide")
    public ResponseEntity<ResponseStructure<String>> completeRide(
            @RequestParam int bookingId,
            @RequestParam String otp) {

        return ResponseEntity.ok(
                bookingService.completeRide(bookingId, otp)
        );
    }
}

