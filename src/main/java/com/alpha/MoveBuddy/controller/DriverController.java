package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.DTO.RideCompletionDTO;
import com.alpha.MoveBuddy.DTO.UpiDTO;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.service.DriverService;

@RestController
public class DriverController {
	@Autowired
	private DriverService ds;
	
	@PostMapping("/savedriver")
	public ResponseEntity<ResponseStructure<Driver>> saveRegisterDriverVehicleDTO (@RequestBody RegisterDriverVehicleDTO driverDTO) {
	return	ds.saveDriverDTO(driverDTO);
		
	}

	@GetMapping("/finddriver/{mobileno}")
	public ResponseEntity<ResponseStructure<Driver>> getDriverByMobile(@PathVariable long mobileno) {
	    return ds.findDriverByMobile(mobileno);
	}
	
	@DeleteMapping("/deletedriver/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> deleteDriver(@PathVariable long mobileNo) {
        return ds.deleteDriver(mobileNo);
    }

	@PutMapping("/updatedrivervehicleloc")
	public ResponseEntity<ResponseStructure<String>> updateLocation(
	        @RequestParam long mobileNo,
	        @RequestParam String latitude,
	        @RequestParam String longitude) {

	    return ds.updateDriverLocation(mobileNo, latitude, longitude);
	}
	
	
	@PutMapping("/completeride")
    public ResponseEntity<ResponseStructure<RideCompletionDTO>> completeRide(@RequestParam int bookingId , @RequestParam String paymentType) {

        return ds.completeRide(bookingId, paymentType);
    }
	
	
	@GetMapping("/seeAllbookinghistory")
	public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(@RequestParam long mobileNo){
		return ds.seeAllBookingHistory(mobileNo);
	}
	
	@PutMapping("/drivercancillation")
	public void cancelBooking(int id, int bookingId) {
		ds.cancelBooking(id, bookingId);
	}


}
