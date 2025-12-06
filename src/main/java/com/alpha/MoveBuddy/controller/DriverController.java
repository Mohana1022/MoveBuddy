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

import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.service.DriverService;

@RestController
public class DriverController {
	@Autowired
	private DriverService ds;
	
	@PostMapping("/savedriver")
	public Driver saveRegisterDriverVehicleDTO (@RequestBody RegisterDriverVehicleDTO driverDTO) {
	return	ds.saveDriverDTO(driverDTO);
		
	}

	@GetMapping("/finddriver/{mobileno}")
    public ResponseEntity<Driver> getDriverByMobile(@PathVariable long mobileno) {
        Driver driver = ds.findDriverByMobile(mobileno);
        return ResponseEntity.ok(driver);
    }
	
	@DeleteMapping("/deletedriver/{mobileNo}")
    public String deleteDriver(@PathVariable long mobileNo) {
        return ds.deleteDriver(mobileNo);
    }
	
<<<<<<< HEAD

=======
	@PutMapping("/updatedrivervehicleloc")
	public ResponseEntity<String> updateLocation(@RequestParam long mobileNo,@RequestParam String latitude,@RequestParam String longitude) {

	    String result = ds.updateDriverLocation(mobileNo, latitude, longitude);
	    return ResponseEntity.ok(result);
	}
>>>>>>> e57855cd62532d5664d8ae325fd013aec1de8f90
}
