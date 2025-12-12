package com.alpha.MoveBuddy.service;

import java.util.Map;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.ResponseStructure;
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


    // -------------------------------------
    // GET CITY NAME (same – no change)
    // -------------------------------------
    public String getCityName(String string, String string2) {

        String url = "https://us1.locationiq.com/v1/reverse?key=" + apiKey +
                "&lat=" + string + "&lon=" + string2 + "&format=json";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> address = (Map<String, Object>) response.get("address");

        if (address.get("city") != null)
            return address.get("city").toString();
        else if (address.get("town") != null)
            return address.get("town").toString();
        else if (address.get("village") != null)
            return address.get("village").toString();
        else
            return "Unknown";
    }



    // ---------------------------------------------------------
    // SAVE DRIVER + VEHICLE  (returns ResponseStructure<Driver>)
    // ---------------------------------------------------------
    public ResponseEntity<ResponseStructure<Driver>> saveDriverDTO(RegisterDriverVehicleDTO dto) {

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
        v.setAvgSpeed(dto.getAverageSpeed());
        v.setDriver(d);
        d.setVehicle(v);

        Driver savedDriver = dr.save(d);

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver saved successfully");
        rs.setData(savedDriver);

        return ResponseEntity.ok(rs);
    }



    // ---------------------------------------------------------
    // FIND DRIVER BY MOBILE  (returns ResponseStructure<Driver>)
    // ---------------------------------------------------------
    public ResponseEntity<ResponseStructure<Driver>> findDriverByMobile(long mobileNo) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with mobile: " + mobileNo));

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver found successfully");
        rs.setData(driver);

        return ResponseEntity.ok(rs);
    }



    // ---------------------------------------------------------
    // DELETE DRIVER  (returns ResponseStructure<String>)
    // ---------------------------------------------------------
    public ResponseEntity<ResponseStructure<String>> deleteDriver(long mobileNo) {

        ResponseStructure<String> rs = new ResponseStructure<>();

        Driver driver = dr.findByMobileno(mobileNo).orElse(null);

        if (driver != null) {
            dr.delete(driver);

            rs.setStatuscode(200);
            rs.setMessage("Driver deleted successfully");
            rs.setData("Deleted");

            return ResponseEntity.ok(rs);
        }

        rs.setStatuscode(404);
        rs.setMessage("Driver not found");
        rs.setData("Not Found");

        return ResponseEntity.status(404).body(rs);
    }



    // ---------------------------------------------------------
    // UPDATE DRIVER LOCATION (returns ResponseStructure<String>)
    // ---------------------------------------------------------
    public ResponseEntity<ResponseStructure<String>> updateDriverLocation(long mobileNo,
                                                                          String latitude,
                                                                          String longitude) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with mobile: " + mobileNo));

        String city = getCityName(latitude, longitude);

        if (driver.getVehicle() != null) {
            Vehicle v = driver.getVehicle();
            v.setCurrentCity(city);

            vr.save(v);
        }

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver location updated successfully");
        rs.setData("Updated to: " + city);

        return ResponseEntity.ok(rs);
    }


}
