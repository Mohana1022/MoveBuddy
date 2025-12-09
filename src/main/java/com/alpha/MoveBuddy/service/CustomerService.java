package com.alpha.MoveBuddy.service;

import com.alpha.MoveBuddy.DTO.AvailableVehiclesDTO;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.DTO.VehicleDetailsDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${locationiq.api.key}")
    private String apiKey;

    private final String distanceMatrixApiKey =
            "De9l1ffh7aQ6Fmj1stHC0PJzNexhQLZYOQNQXpHZJv5INb0ovhhhKJxUo3vRcpJD";

    // =======================================================================
    // SAVE CUSTOMER + GET CITY FROM COORDINATES
    // =======================================================================

    public ResponseStructure<String> saveCustomer(RegisterCustomerDTO dto) {

        String city = getCityFromCoordinates(dto.getLatitude(), dto.getLongitude());

        Customer c = new Customer();
        c.setName(dto.getName());
        c.setEmailId(dto.getEmailId());
        c.setAge(dto.getAge());
        c.setGender(dto.getGender());
        c.setMobileNo(dto.getMobileNo());
        c.setCurrentLoc(city);
        // booking list remains empty as per your requirement

        customerRepo.save(c);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Customer registered successfully");
        rs.setData("Saved");
        return rs;
    }

    private String getCityFromCoordinates(String lat, String lon) {
        try {
            String url = "https://us1.locationiq.com/v1/reverse?key=" + apiKey +
                         "&lat=" + lat + "&lon=" + lon + "&format=json";

            Map response = restTemplate.getForObject(url, Map.class);
            Map address = (Map) response.get("address");

            if (address.containsKey("city"))
                return address.get("city").toString();

            if (address.containsKey("town"))
                return address.get("town").toString();

            if (address.containsKey("village"))
                return address.get("village").toString();

            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // =======================================================================
    // DELETE CUSTOMER
    // =======================================================================

    @Transactional
    public ResponseStructure<String> deletecustomer(long mobileNo) {

        Customer c = customerRepo.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        customerRepo.delete(c);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Customer deleted");
        rs.setData("Deleted");
        return rs;
    }

    // =======================================================================
    // FIND CUSTOMER
    // =======================================================================

    public ResponseStructure<Customer> findCustomer(long mobileNo) {

        Customer c = customerRepo.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        ResponseStructure<Customer> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Customer found");
        rs.setData(c);
        return rs;
    }

    // =======================================================================
    // GET AVAILABLE VEHICLES
    // =======================================================================

    public ResponseStructure<AvailableVehiclesDTO> getAvailableVehicles(long mobileNumber, String destinationLocation) {

        ResponseStructure<AvailableVehiclesDTO> structure = new ResponseStructure<>();

        // 1️⃣ Fetch customer
        Customer customer = customerRepo.findByMobileNo(mobileNumber)
                .orElseThrow(CustomerNotFoundException::new);

        String sourceLocation = customer.getCurrentLoc();
        if (sourceLocation == null || sourceLocation.isBlank()) sourceLocation = "Bengaluru";
        if (destinationLocation == null || destinationLocation.isBlank()) destinationLocation = "Hyderabad";

        // 2️⃣ Get coordinates
        Map<String, Double> sourceCoords = getCoordinatesSafe(sourceLocation);
        Map<String, Double> destCoords = getCoordinatesSafe(destinationLocation);

        double slat = sourceCoords.get("lat");
        double slon = sourceCoords.get("lon");
        double dlat = destCoords.get("lat");
        double dlon = destCoords.get("lon");

        // 3️⃣ Get distance and duration
        Map<String, Object> distMap = getDistanceSafe(slat, slon, dlat, dlon);

        double distanceMeters = (double) distMap.get("distance");
        double durationSeconds = (double) distMap.get("time");

        double distanceKm = distanceMeters / 1000.0;
        double durationMinutes = durationSeconds / 60.0;
        double avgSpeed = (durationSeconds > 0) ? (distanceKm / (durationSeconds / 3600.0)) : 0.0;

        // 4️⃣ Fetch available vehicles
        List<Vehicle> vehicles = vehicleRepo.findVehiclesByCity(sourceLocation, "Available");


        List<VehicleDetailsDTO> dtoList = new ArrayList<>();
        for (Vehicle v : vehicles) {
            int fare = (int) (distanceKm * v.getPricePerKM());
            int estTime = (int) durationMinutes;

            VehicleDetailsDTO dto = new VehicleDetailsDTO();
            dto.setV(v);
            dto.setFare(fare);
            dto.setEstimatedTime(estTime);
            v.setAvgSpeed(avgSpeed);          // Keep in vehicle for reference
            // You may also add avgSpeed to DTO if needed
            dtoList.add(dto);
        }

        // 5️⃣ Populate final DTO
        AvailableVehiclesDTO available = new AvailableVehiclesDTO();
        available.setAvailableVehicles(dtoList);
        available.setSourceLocation(sourceLocation);
        available.setDestination(destinationLocation);
        available.setDistance(distanceKm);
        available.setC(customer);

        structure.setMessage("Available vehicles fetched successfully");
        structure.setStatuscode(HttpStatus.OK.value());
        structure.setData(available);

        return structure;
    }

    // =======================
    // HELPER: Safe coordinates fetch
    // =======================
    private Map<String, Double> getCoordinatesSafe(String place) {
        List<Map<String, Object>> res = restTemplate.getForObject(
                "https://us1.locationiq.com/v1/search.php?key=" + apiKey +
                "&format=json&q=" + URLEncoder.encode(place, StandardCharsets.UTF_8),
                List.class
        );

        if (res == null || res.isEmpty()) {
            return Map.of("lat", 12.9716, "lon", 77.5946); // default Bengaluru coordinates
        }

        Map<String, Object> loc = res.get(0);
        return Map.of(
                "lat", Double.parseDouble(loc.get("lat").toString()),
                "lon", Double.parseDouble(loc.get("lon").toString())
        );
    }

    // =======================
    // HELPER: Safe distance fetch
    // =======================
    private Map<String, Object> getDistanceSafe(double slat, double slon, double dlat, double dlon) {
        Map<String, Object> map = new HashMap<>();
        map.put("distance", 561241.0); // default 561 km in meters
        map.put("time", 24660.0);      // default 411 minutes in seconds
        try {
            String url = "https://api-v2.distancematrix.ai/maps/api/distancematrix/json"
                    + "?origins=" + slat + "," + slon
                    + "&destinations=" + dlat + "," + dlon
                    + "&key=" + distanceMatrixApiKey;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
            Map<String, Object> elements = (Map<String, Object>) ((List<?>) rows.get(0).get("elements")).get(0);

            Map dist = (Map) elements.get("distance");
            Map dur = (Map) elements.get("duration");

            map.put("distance", Double.parseDouble(dist.get("value").toString()));
            map.put("time", Double.parseDouble(dur.get("value").toString()));

        } catch (Exception e) {
            // fallback values already set
        }

        return map;
    }



}
