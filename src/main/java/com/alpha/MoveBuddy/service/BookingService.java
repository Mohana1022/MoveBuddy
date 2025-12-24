package com.alpha.MoveBuddy.service;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.Repository.*;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.*;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // BOOK VEHICLE (OTP GENERATED HERE)
    @Transactional
    public ResponseStructure<Booking> bookVehicle(Long mobileNo, BookingDTO dto) {

        Customer customer = customerRepository.findByMobileNo(mobileNo)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleid())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setSourceLoc(dto.getSourceLoc());
        booking.setDestinationLoc(dto.getDestinationLoc());
        booking.setDistanceTravelled(dto.getDistanceTravelled());
        booking.setFare(dto.getFare());
        booking.setEstimatedTime(dto.getEstimatedTime());

        // ✅ OTP generation
        String otp = String.valueOf(1000 + new Random().nextInt(9999));
        booking.setOtp(otp);
        booking.setBookingStatus("BOOKED");

        bookingRepository.save(booking);

        customer.getBookinglist().add(booking);
        customer.setBookingflag(true);
        customerRepository.save(customer);

        if (vehicle.getDriver() != null) {
            Driver driver = vehicle.getDriver();
            if (driver.getBookings() == null)
                driver.setBookings(new ArrayList<>());
            driver.getBookings().add(booking);
        }

        vehicle.setAvailableStatus("BOOKED");
        vehicleRepository.save(vehicle);

        // OTP sent only to customer (for now console)
        System.out.println("Customer OTP: " + otp);

        ResponseStructure<Booking> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Vehicle booked successfully");
        rs.setData(booking);

        return rs;
    }

    // COMPLETE RIDE (OTP VALIDATION)
    public ResponseStructure<String> completeRide(int bookingId, String enteredOtp) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        ResponseStructure<String> rs = new ResponseStructure<>();

        if (!booking.getOtp().equals(enteredOtp)) {
            rs.setStatuscode(HttpStatus.BAD_REQUEST.value());
            rs.setMessage("Invalid OTP");
            return rs;
        }

        booking.setBookingStatus("COMPLETED");
        booking.setOtp(null);
        bookingRepository.save(booking);

        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Ride completed successfully");
        rs.setData("SUCCESS");

        return rs;
    }
}

