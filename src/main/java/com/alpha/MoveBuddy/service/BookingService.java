package com.alpha.MoveBuddy.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Vehicle;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    //  Book a vehicle
    @Transactional
    public ResponseStructure<Booking> bookVehicle(Long customerMobile, BookingDTO dto) {

        Customer customer = customerRepository.findByMobileNo(customerMobile)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleid())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setSourceLoc(dto.getSourceLoc());
        booking.setDestinationLoc(dto.getDestinationLoc());
        booking.setDistanceTravelled(dto.getDistanceTravelled());
        booking.setFare(dto.getFare());
        booking.setEstimatedTime(dto.getEstimatedTime());
        booking.setBookingStatus("booked");

        bookingRepository.save(booking);

        // Add booking to customer and set flag
        customer.getBookinglist().add(booking);
        customer.setBookingflag(true);
        customerRepository.save(customer);

        // Add booking to driver
        Driver driver = vehicle.getDriver();
        if (driver != null) {
            if (driver.getBookings() == null)
                driver.setBookings(new ArrayList<>());
            driver.getBookings().add(booking);
        }

        // Update vehicle status
        vehicle.setAvailableStatus("booked");
        vehicleRepository.save(vehicle);

        // Response
        ResponseStructure<Booking> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Vehicle successfully booked");
        rs.setData(booking);

        return rs;
    }
    
}
