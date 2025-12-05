package com.alpha.MoveBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Vehicle;
@Service
public class DriverService {

	@Autowired
	private DriverRepository dr;
	


	public Driver saveDriverDTO(RegisterDriverVehicleDTO driverDTO) {
		Vehicle v=new Vehicle();
		v.setName(driverDTO.getVehicleName());
		v.setVehicleNo(driverDTO.getVehicleNo());
		v.setType(driverDTO.getVehicleType());
		v.setModel(driverDTO.getModel());
		v.setCapacity(driverDTO.getVehicleCapacity());
		v.setCurrentCity(driverDTO.getLatitude());
		v.setCurrentCity(driverDTO.getLongitude());
		v.setPricePerKM(driverDTO.getPricePerKM());
		
		
		Driver d=new Driver();
		d.setLicenseNo(driverDTO.getLicenseNo());
		d.setUpiid(driverDTO.getUpiID());
		d.setName(driverDTO.getDriverName());
		d.setAge(driverDTO.getAge());
		d.setMobileno(driverDTO.getMobileNo());
		d.setGender(driverDTO.getGender());
		d.setMailid(driverDTO.getMailId());
		
		v.setDriver(d);
		d.setVehicle(v);
		
		return dr.save(d);
		
		
	}

}
