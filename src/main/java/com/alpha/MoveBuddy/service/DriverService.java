package com.alpha.MoveBuddy.service;

import java.util.Map;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.DriverNotFoundException;

@Service
public class DriverService {

    @Autowired
    private DriverRepository dr;
    @Autowired
    private VehicleRepository vr;

    @Value("${locationiq.api.key}")
    private String apiKey;

    public String getCityName(String string, String string2) {

        String url = "https://us1.locationiq.com/v1/reverse?key=" + apiKey +
                     "&lat=" + string + "&lon=" + string2 + "&format=json";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> address = (Map<String, Object>) response.get("address");

        // Try city, town, village (sometimes city is not present)
        if (address.get("city") != null)
            return address.get("city").toString();
        else if (address.get("town") != null)
            return address.get("town").toString();
        else if (address.get("village") != null)
            return address.get("village").toString();
        else
            return "Unknown";
    }

    public Driver saveDriverDTO(RegisterDriverVehicleDTO dto) {

        Driver d = new Driver();
        d.setLicenseNo(dto.getLicenseNo());
        d.setUpiid(dto.getUpiID());
        d.setName(dto.getDriverName());
        d.setAge(dto.getAge());
        d.setMobileno(dto.getMobileNo());
        d.setGender(dto.getGender());
        d.setMailid(dto.getMailId());

        String city = getCityName(dto.getLatitude(), dto.getLongitude());

        Vehicle v = new Vehicle();
        v.setName(dto.getVehicleName());
        v.setVehicleNo(dto.getVehicleNo());
        v.setType(dto.getVehicleType());
        v.setModel(dto.getModel());
        v.setCapacity(dto.getVehicleCapacity());
        v.setCurrentCity(city); 
        v.setPricePerKM(dto.getPricePerKM());

        
        v.setDriver(d);
        d.setVehicle(v);

        
        return dr.save(d);
    }
    
    
    public Driver findDriverByMobile(long mobileNo) {
        return dr.findByMobileno(mobileNo).orElseThrow(() -> 
                     new DriverNotFoundException("Driver not found with mobile number: " + mobileNo));
    }

    
    public String deleteDriver(long mobileNo) {

        Driver driver = dr.findByMobileno(mobileNo).orElse(null);

        if (driver != null) {
            dr.delete(driver);
            return "Driver deleted successfully";
        }

        return "Driver not found";
    }


<<<<<<< HEAD
=======
	public String updateDriverLocation(long mobileNo, String latitude, String longitude) {
		
		
        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException(
                        "Driver not found with mobile: " + mobileNo));

       
        String city = getCityName(latitude, longitude);

       
        if (driver.getVehicle() != null) {
            Vehicle v = driver.getVehicle();
            v.setCurrentCity(city);

           
            vr.save(v);
        }

        return "Driver location updated to city: " + city;

	}

    
>>>>>>> e57855cd62532d5664d8ae325fd013aec1de8f90
}
