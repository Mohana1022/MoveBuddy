package com.alpha.MoveBuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired
    private MailService mailservice;
    

	 @Autowired
	    private JavaMailSender javamailsender;
	    
   
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

        booking.setRideOtp(generateOtp());     // 🔐 generate
        booking.setOtpVerified(false);
        booking.setOtpGeneratedAt(LocalDateTime.now());
        
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

        mailservice.sendMail("pmohana1022@gmal.com","Booking conformed for" + vehicle.getName(),"Hello Vehicle Bokked successfully");
        
        return rs;
    }

	private String generateOtp() {

		return String.valueOf(1000 + new Random().nextInt(9999));
	}

	public String getRideOtp1(Long customerId, int bookingId) {
		// TODO Auto-generated method stub
		return String.valueOf(1000 + new Random().nextInt(9999));
	}
	// ---------------- GET RIDE OTP FOR CUSTOMER ----------------
    public ResponseEntity<ResponseStructure<String>> getRideOtp(Long customerId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getCustomer() == null ||
        	    !Objects.equals(booking.getCustomer().getId(), customerId)) {
        	    throw new RuntimeException("Booking does not belong to customer");
        	}

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("OTP fetched successfully");
        rs.setData(booking.getRideOtp());

        return ResponseEntity.ok(rs);
    }



	
	
    
}
