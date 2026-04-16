package com.alpha.MoveBuddy.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingservice;

    /**
     * POST /booking/book?mobileNo=9876543210
     * Customer books a vehicle after selecting from the available list.
     */
    @PostMapping("/book")
    public ResponseEntity<ResponseStructure<Booking>> bookVehicle(
            @RequestParam Long mobileNo,
            @Valid @RequestBody BookingDTO bookingdto) {

        ResponseStructure<Booking> response = bookingservice.bookVehicle(mobileNo, bookingdto);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /booking/otp?customerId=1&bookingId=5
     * Customer fetches the ride OTP to hand to the driver.
     */
    @GetMapping("/otp")
    public ResponseEntity<ResponseStructure<String>> getRideOtp(
            @RequestParam Long customerId,
            @RequestParam int bookingId) {
        return bookingservice.getRideOtp(customerId, bookingId);
    }

    /**
     * GET /booking/active?mobileNo=9876543210
     * Customer checks their current active booking.
     */
    @GetMapping("/active")
    public ResponseEntity<ResponseStructure<?>> getActiveBooking(@RequestParam long mobileNo) {
        return bookingservice.getActiveBooking(mobileNo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Booking>> getBookingById(@PathVariable int id) {
        return bookingservice.getBookingById(id);
    }
}
