package com.alpha.MoveBuddy.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.DTO.RideCompletionDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.service.DriverService;

@RestController
public class DriverController {

    @Autowired
    private DriverService ds;

    // ---------------- SAVE DRIVER ----------------
    @PostMapping("/savedriver")
    public ResponseEntity<ResponseStructure<Driver>> saveDriver(
            @RequestBody RegisterDriverVehicleDTO driverDTO) {
        return ds.saveDriverDTO(driverDTO);
    }

    // ---------------- FIND DRIVER ----------------
    @GetMapping("/finddriver/{mobileno}")
    public ResponseEntity<ResponseStructure<Driver>> findDriver(
            @PathVariable long mobileno) {
        return ds.findDriverByMobile(mobileno);
    }

    // ---------------- DELETE DRIVER ----------------
    @DeleteMapping("/deletedriver/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> deleteDriver(
            @PathVariable long mobileNo) {
        return ds.deleteDriver(mobileNo);
    }

    // ---------------- UPDATE DRIVER LOCATION ----------------
    @PutMapping("/updatedrivervehicleloc")
    public ResponseEntity<ResponseStructure<String>> updateLocation(
            @RequestParam long mobileNo,
            @RequestParam String latitude,
            @RequestParam String longitude) {
        return ds.updateDriverLocation(mobileNo, latitude, longitude);
    }

    // ---------------- COMPLETE RIDE ----------------
    @PutMapping("/completeride")
    public ResponseEntity<ResponseStructure<RideCompletionDTO>> completeRide(
            @RequestParam int bookingId,
            @RequestParam String paymentType) {
        return ds.completeRide(bookingId, paymentType);
    }

    // ---------------- BOOKING HISTORY ----------------
    @GetMapping("/seeAllbookinghistory")
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(
            @RequestParam long mobileNo) {
        return ds.seeAllBookingHistory(mobileNo);
    }

    // ---------------- DRIVER CANCELLATION (UPDATED) ----------------
    @PutMapping("/drivercancellation")
    public ResponseEntity<ResponseStructure<String>> cancelBooking(
            @RequestParam int driverId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) {

        ds.cancelBooking(driverId, bookingDate);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Booking cancelled by driver");
        rs.setData("CANCELLED");

        return ResponseEntity.ok(rs);
    }
}
