package com.alpha.MoveBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.service.DriverService;
import com.alpha.MoveBuddy.service.LocationService;

@RestController
public class DriverController {
	@Autowired
	private DriverService ds;
	
	@PostMapping("/savedriver")
	public Driver saveRegisterDriverVehicleDTO (@RequestBody RegisterDriverVehicleDTO driverDTO) {
	return	ds.saveDriverDTO(driverDTO);
		
	}

	
}
