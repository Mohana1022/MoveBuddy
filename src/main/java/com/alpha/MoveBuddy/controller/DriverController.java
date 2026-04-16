package com.alpha.MoveBuddy.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.DTO.RideCompletionDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService ds;

    /** GET /driver/profile/{mobileNo} - Get driver profile */
    @GetMapping("/profile/{mobileNo}")
    public ResponseEntity<ResponseStructure<Driver>> findDriver(@PathVariable long mobileNo) {
        return ds.findDriverByMobile(mobileNo);
    }

    /** DELETE /driver/delete/{mobileNo} - Delete a driver */
    @DeleteMapping("/delete/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> deleteDriver(@PathVariable long mobileNo) {
        return ds.deleteDriver(mobileNo);
    }

    /**
     * PUT /driver/update-location
     * Update the driver's current city by passing GPS coordinates.
     */
    @PutMapping("/update-location")
    public ResponseEntity<ResponseStructure<String>> updateLocation(
            @RequestParam long mobileNo,
            @RequestParam String latitude,
            @RequestParam String longitude) {
        return ds.updateDriverLocation(mobileNo, latitude, longitude);
    }

    /**
     * PUT /driver/toggle-availability/{mobileNo}
     * Toggles driver between "Available" and "Offline".
     */
    @PutMapping("/toggle-availability/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> toggleAvailability(@PathVariable long mobileNo) {
        return ds.toggleAvailability(mobileNo);
    }

    /**
     * GET /driver/incoming-rides/{mobileNo}
     * Returns all PENDING rides in the driver's city.
     */
    @GetMapping("/incoming-rides/{mobileNo}")
    public ResponseEntity<ResponseStructure<List<Booking>>> getIncomingRides(@PathVariable long mobileNo) {
        return ds.getIncomingRides(mobileNo);
    }

    /**
     * PUT /driver/complete-ride
     * Marks a ride as COMPLETED after OTP verification.
     */
    @PutMapping("/complete-ride")
    public ResponseEntity<ResponseStructure<RideCompletionDTO>> completeRide(
            @RequestParam int bookingId,
            @RequestParam String paymentType) {
        return ds.completeRide(bookingId, paymentType);
    }

    /** GET /driver/booking-history/{mobileNo} - Booking history for driver */
    @GetMapping("/booking-history/{mobileNo}")
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(
            @PathVariable long mobileNo) {
        return ds.seeAllBookingHistory(mobileNo);
    }

    /**
     * PUT /driver/cancel-ride
     * Driver cancels the booking on a specific date.
     */
    @PutMapping("/cancel-ride")
    public ResponseEntity<ResponseStructure<String>> cancelBooking(
            @RequestParam int driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) {

        ds.cancelBooking(driverId, bookingDate);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Booking cancelled by driver");
        rs.setData("CANCELLED");

        return ResponseEntity.ok(rs);
    }

    /**
     * POST /driver/validate-otp
     * Driver validates the OTP provided by the customer to start the ride.
     */
    @PostMapping("/validate-otp")
    public ResponseEntity<ResponseStructure<String>> validateOtp(
            @RequestParam int bookingId,
            @RequestParam String otp) {
        return ds.validateRideOtp(bookingId, otp);
    }
}
