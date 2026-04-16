package com.alpha.MoveBuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MailService mailservice;

    // ---- BOOK A VEHICLE ----
    @Transactional
    public ResponseStructure<Booking> bookVehicle(Long customerMobile, BookingDTO dto) {

        Customer customer = customerRepository.findByMobileNo(customerMobile)
                .orElseThrow(() -> new CustomerNotFoundException());

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleid())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (customer.isBookingflag()) {
            throw new RuntimeException("Customer already has an active booking");
        }

        if (!"Available".equalsIgnoreCase(vehicle.getAvailableStatus())) {
            throw new RuntimeException("Vehicle is not available for booking");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setSourceLoc(dto.getSourceLoc());
        booking.setDestinationLoc(dto.getDestinationLoc());
        booking.setDistanceTravelled(dto.getDistanceTravelled());
        booking.setFare(dto.getFare());
        booking.setEstimatedTime(dto.getEstimatedTime());
        booking.setBookingStatus("booked");
        booking.setRideOtp(generateOtp());
        booking.setOtpVerified(false);
        booking.setOtpGeneratedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        customer.getBookinglist().add(booking);
        customer.setBookingflag(true);
        customerRepository.save(customer);

        Driver driver = vehicle.getDriver();
        if (driver != null) {
            if (driver.getBookings() == null)
                driver.setBookings(new ArrayList<>());
            driver.getBookings().add(booking);
        }

        vehicle.setAvailableStatus("booked");
        vehicleRepository.save(vehicle);

        // Send confirmation email if customer has email
        if (customer.getEmailId() != null && !customer.getEmailId().isBlank()) {
            try {
                mailservice.sendMail(
                        customer.getEmailId(),
                        "MoveBuddy – Ride Booking Confirmed 🚗",
                        "Hi " + customer.getName() + ",\n\n" +
                        "Your ride has been booked!\n" +
                        "From: " + dto.getSourceLoc() + "\n" +
                        "To: " + dto.getDestinationLoc() + "\n" +
                        "Estimated Fare: ₹" + dto.getFare() + "\n" +
                        "Ride OTP: " + booking.getRideOtp() + "\n\n" +
                        "Share this OTP with your driver to start the ride.\n\n" +
                        "– MoveBuddy Team"
                );
            } catch (Exception e) {
                // Log but don't fail booking if email fails
                System.err.println("Email sending failed: " + e.getMessage());
            }
        }

        ResponseStructure<Booking> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Vehicle successfully booked");
        rs.setData(booking);

        return rs;
    }

    // ---- GET RIDE OTP ----
    public ResponseEntity<ResponseStructure<String>> getRideOtp(Long customerId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getCustomer() == null ||
                !Objects.equals(booking.getCustomer().getId(), Math.toIntExact(customerId))) {
            throw new RuntimeException("Booking does not belong to this customer");
        }

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("OTP fetched successfully");
        rs.setData(booking.getRideOtp());

        return ResponseEntity.ok(rs);
    }

    // ---- GET ACTIVE BOOKING ----
    public ResponseEntity<ResponseStructure<?>> getActiveBooking(long mobileNo) {
        Customer customer = customerRepository.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        Booking booking = bookingRepository.findActiveBookingByCustomerId(mobileNo);

        if (booking == null) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatuscode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("No active booking found");
            rs.setData("NO_ACTIVE_BOOKING");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        ResponseStructure<Booking> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Active booking fetched");
        rs.setData(booking);

        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<ResponseStructure<Booking>> getBookingById(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        ResponseStructure<Booking> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Booking details fetched");
        rs.setData(booking);

        return ResponseEntity.ok(rs);
    }

    // ---- HELPER: Generate 4-digit OTP ----
    private String generateOtp() {
        return String.format("%04d", 1000 + new Random().nextInt(9000));
    }
}
