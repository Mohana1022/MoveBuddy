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

	@Service
	public class BookingService {

		@Autowired
		private CustomerRepository customerRepository;

		@Autowired
		private DriverRepository driverrepository;
		
	    @Autowired
	    private VehicleRepository vehicleRepository;
		
		@Autowired
		private BookingRepository bookingRepository;


		public ResponseStructure<Booking> bookVehicle(Long customerMobile, BookingDTO dto) {

	        // 1. Find customer by mobile
	        Customer customer = customerRepository.findByMobileNo(customerMobile)
	                .orElseThrow(() -> new RuntimeException("Customer not found"));

	        // 2. Find vehicle by id
	        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleid())
	                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

	        // 3. Create booking
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
	       
	        customer.getBookinglist().add(booking);
//	        d.setbooking(booking);
	        
	        Driver driver = vehicle.getDriver(); // get driver from vehicle
	        if (driver != null) {
	            if (driver.getBookings() == null)
	                driver.setBookings(new ArrayList<>());

	            driver.getBookings().add(booking);
	        }
	      
	        vehicle.setAvailableStatus("booked");
	        
	        customerRepository.save(customer);
	        vehicleRepository.save(vehicle);
	    
	       
//	        Payment payment = new Payment();
//	        payment.setCustomer(customer);
//	        payment.setVehicle(vehicle);
//	        payment.setBooking(booking);
//	        payment.setAmount(dto.getFare());
	//
//	        // PAYMENT TYPE SHOULD BE e.g., "UPI", "CASH", "CARD"
//	        payment.setPaymenttype("not paid"); // since no payment made yet
	//
//	        booking.setPayement(payment); // correct setter name

	        // 5️⃣ UPDATE VEHICLE STATUS
	 
	        ResponseStructure<Booking> rs = new ResponseStructure<Booking>();
			rs.setStatuscode(HttpStatus.OK.value());
			rs.setMessage("successfully booked");
			rs.setData(booking);
			return rs;
		}
	}


