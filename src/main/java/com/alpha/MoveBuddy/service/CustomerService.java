package com.alpha.MoveBuddy.service;

import com.alpha.MoveBuddy.DTO.AvailableVehiclesDTO;
import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.CustomerActiveBookingDTO;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.DTO.RideDetailsDTO;
import com.alpha.MoveBuddy.DTO.VehicleDetailsDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.BookingNotFoundException;
import com.alpha.MoveBuddy.exception.CoordinatesNotFoundException;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;
import com.alpha.MoveBuddy.exception.DistanceCalculationFailedException;
import com.alpha.MoveBuddy.exception.InvalidLocationException;
import com.alpha.MoveBuddy.exception.NoCurrentBookingException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Driver;
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

    
    // SAVE CUSTOMER + GET CITY FROM COORDINATES
    

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

   
    // DELETE CUSTOMER
    

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

    // FIND CUSTOMER
   
    public ResponseStructure<Customer> findCustomer(long mobileNo) {

        Customer c = customerRepo.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        ResponseStructure<Customer> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Customer found");
        rs.setData(c);
        return rs;
    }
    
    // GET AVAILABLE VEHICLES

    public ResponseStructure<AvailableVehiclesDTO> getAvailableVehicles(long mobileNumber, String destinationLocation) {

        ResponseStructure<AvailableVehiclesDTO> structure = new ResponseStructure<>();

        //  Fetch customer
        Customer customer = customerRepo.findByMobileNo(mobileNumber).orElseThrow(()-> new CustomerNotFoundException());

        String sourceLocation = customer.getCurrentLoc();

        if (sourceLocation == null || sourceLocation.isBlank()
                || destinationLocation == null || destinationLocation.isBlank()
                || sourceLocation.equalsIgnoreCase(destinationLocation)) {

            throw new InvalidLocationException();
        }


        //  Get coordinates
        
        Map<String, Double> sourceCoords = getCoordinatesSafe(sourceLocation);
        Map<String, Double> destCoords = getCoordinatesSafe(destinationLocation);

        // ❌ Destination not found
        if (destCoords == null || destCoords.isEmpty()|| !destCoords.containsKey("lat")|| !destCoords.containsKey("lon")) {

            throw new InvalidLocationException();
        }
        
        double slat = sourceCoords.get("lat");
        double slon = sourceCoords.get("lon");
        double dlat = destCoords.get("lat");
        double dlon = destCoords.get("lon");


        //  Distance + duration
        Map<String, Object> distMap = getDistanceSafe(slat, slon, dlat, dlon);

        double distanceMeters = (double) distMap.get("distance");
        double durationSeconds = (double) distMap.get("time");

        double distanceKm = distanceMeters / 1000.0;
        double durationMinutes = durationSeconds / 60.0;
        double avgSpeed = 
        		(distanceKm / (durationMinutes / 60.0));
                

     //  Fetch available vehicles
        List<Vehicle> vehicles = vehicleRepo.findVehiclesByCity(sourceLocation, "Available");

        List<VehicleDetailsDTO> dtoList = new ArrayList<>();
        for (Vehicle v : vehicles) {
            // Fare calculation
            int fare = (int) (distanceKm * v.getPricePerKM());

            // Estimated time in minutes (rounded)
            int estTime = (int) durationMinutes;

            // Calculate actual average speed (km/h)
            double actualSpeed = (distanceKm / (durationMinutes / 60.0));
            v.setAvgSpeed(actualSpeed);

            // Prepare DTO
            VehicleDetailsDTO dto = new VehicleDetailsDTO();
            dto.setV(v);
            dto.setFare(fare);
            dto.setEstimatedTime(estTime);
            dto.setAveragespeed(actualSpeed);


            dtoList.add(dto);
        }



        //  Create output DTO
        AvailableVehiclesDTO available = new AvailableVehiclesDTO();
        available.setAvailableVehicles(dtoList);
        available.setSourceLocation(sourceLocation);
        available.setDestination(destinationLocation);
        available.setDistance(distanceKm);
        available.setC(customer);

        structure.setMessage("Available vehicles fetched successfully");
        structure.setStatuscode(HttpStatus.FOUND.value());
        structure.setData(available);

        return structure;
        }
    


    // HELPER: Safe coordinates fetch
    
    public ResponseStructure<AvailableVehiclesDTO> getAllAvailableVehicles(
            long mobileNumber, String destinationLocation) {

        Customer customer = customerRepo.findByMobileNo(mobileNumber)
                .orElseThrow(CustomerNotFoundException::new);

        String sourceLocation = customer.getCurrentLoc();

        if (sourceLocation == null || sourceLocation.isBlank()
                || destinationLocation == null || destinationLocation.isBlank()
                || sourceLocation.equalsIgnoreCase(destinationLocation)) {

            throw new InvalidLocationException();
        }

        // ✅ STRICT DESTINATION VALIDATION
        Map<String, Double> sourceCoords = getCoordinatesSafe(sourceLocation);
        Map<String, Double> destCoords = getCoordinatesSafe(destinationLocation);

        double slat = sourceCoords.get("lat");
        double slon = sourceCoords.get("lon");
        double dlat = destCoords.get("lat");
        double dlon = destCoords.get("lon");

        Map<String, Object> distMap = getDistanceSafe(slat, slon, dlat, dlon);

        double distanceKm = (double) distMap.get("distance") / 1000.0;
        double durationMinutes = (double) distMap.get("time") / 60.0;

        List<Vehicle> vehicles =
                vehicleRepo.findVehiclesByCity(sourceLocation, "Available");

        List<VehicleDetailsDTO> dtoList = new ArrayList<>();

        for (Vehicle v : vehicles) {

            double avgSpeed = distanceKm / (durationMinutes / 60.0);

            VehicleDetailsDTO dto = new VehicleDetailsDTO();
            dto.setV(v);
            dto.setFare((int) (distanceKm * v.getPricePerKM()));
            dto.setEstimatedTime((int) durationMinutes);
            dto.setAveragespeed(avgSpeed);

            v.setAvgSpeed(avgSpeed);
            dtoList.add(dto);
        }

        AvailableVehiclesDTO available = new AvailableVehiclesDTO();
        available.setAvailableVehicles(dtoList);
        available.setSourceLocation(sourceLocation);
        available.setDestination(destinationLocation);
        available.setDistance(distanceKm);
        available.setC(customer);

        ResponseStructure<AvailableVehiclesDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Available vehicles fetched successfully");
        rs.setData(available);

        return rs;
    }

    /* =====================================================
       LOCATION VALIDATION CORE
       ===================================================== */

    private Map<String, Double> getCoordinatesSafe(String place) {

        String url = "https://us1.locationiq.com/v1/search.php?key=" + apiKey +
                "&format=json&q=" + URLEncoder.encode(place, StandardCharsets.UTF_8);

        List<Map<String, Object>> res = restTemplate.getForObject(url, List.class);

        if (res == null || res.isEmpty()) {
            throw new InvalidLocationException();
        }

        Map<String, Object> loc = res.get(0);

        String type = String.valueOf(loc.get("type"));

        Set<String> allowedTypes = Set.of(
                "city", "town", "village", "administrative", "state", "county"
        );

        if (!allowedTypes.contains(type.toLowerCase())) {
            throw new InvalidLocationException(
            );
        }

        double lat = Double.parseDouble(loc.get("lat").toString());
        double lon = Double.parseDouble(loc.get("lon").toString());

        return Map.of("lat", lat, "lon", lon);
    }

    /* =====================================================
       DISTANCE CALCULATION
       ===================================================== */

    private Map<String, Object> getDistanceSafe(
            double slat, double slon, double dlat, double dlon) {

        String url = "https://api-v2.distancematrix.ai/maps/api/distancematrix/json"
                + "?origins=" + slat + "," + slon
                + "&destinations=" + dlat + "," + dlon
                + "&key=" + distanceMatrixApiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null)
            throw new DistanceCalculationFailedException("Distance API failed");

        Map element =
                (Map) ((List) ((Map) ((List) response.get("rows")).get(0))
                        .get("elements")).get(0);

        Map distance = (Map) element.get("distance");
        Map duration = (Map) element.get("duration");

        return Map.of(
                "distance", Double.parseDouble(distance.get("value").toString()),
                "time", Double.parseDouble(duration.get("value").toString())
        );
    }

 
//       CUSTOMER ACTIVE BOOKING
       

    public ResponseEntity<ResponseStructure<CustomerActiveBookingDTO>>CustomerSeeActiveBooking(long mobileNo) {

        Customer customer = customerRepo.findByMobileNo(mobileNo).orElseThrow(CustomerNotFoundException::new);

        Booking booking =bookingRepo.findActiveBookingByCustomerId(customer.getMobileNo());

        if (booking == null)
        	throw new NoCurrentBookingException();

        CustomerActiveBookingDTO dto = new CustomerActiveBookingDTO();
        dto.setCustomername(customer.getName());
        dto.setCustomerMobile(customer.getMobileNo());
        dto.setBooking(booking);
        dto.setCurrentLocation(booking.getVehicle().getCurrentCity());

        ResponseStructure<CustomerActiveBookingDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Active booking fetched successfully");
        rs.setData(dto);

        return new ResponseEntity<>(rs, HttpStatus.OK);
    }
    
// See Customer Booking History
    
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeCustomerBookingHistory(long mobileNo) {

        // 1. Fetch the customer
        Customer customer = customerRepo.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        // 2. Fetch bookings of the customer
        List<Booking> bookings = bookingRepo.findByCustomerMobileNo(mobileNo);

        // 3. Filter only completed bookings
        List<Booking> completedBookings = bookings.stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getBookingStatus()))
                .toList();

        // 4. If no completed bookings exist, throw exception
        if (completedBookings.isEmpty()) {
            throw new NoCurrentBookingException();
        }

        // 5. Build history DTO
        List<RideDetailsDTO> history = new ArrayList<>();
        double totalAmount = 0;

        for (Booking b : completedBookings) {
            RideDetailsDTO dto = new RideDetailsDTO();
            dto.setFromLoc(b.getSourceLoc());
            dto.setToLoc(b.getDestinationLoc());
            dto.setDistance(b.getDistanceTravelled());
            dto.setFare(b.getFare());

            history.add(dto);
            totalAmount += b.getFare();
        }

        BookingHistoryDto bookingHistoryDto = new BookingHistoryDto();
        bookingHistoryDto.setHistory(history);
        bookingHistoryDto.setTotalAmount(totalAmount);

        // 6. Wrap in ResponseStructure
        ResponseStructure<BookingHistoryDto> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Customer booking history fetched successfully");
        response.setData(bookingHistoryDto);

        return ResponseEntity.ok(response);
    }


    /* =====================================================
       CUSTOMER CANCELLATION
       ===================================================== */

    @Transactional
    public ResponseStructure<Customer> customerCancellation(
            int bookingId, int customerId) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        customer.setPenality(customer.getPenality() + 1);
        booking.setBookingStatus("cancelled by customer");

        customerRepo.save(customer);
        bookingRepo.save(booking);

        ResponseStructure<Customer> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Booking cancelled successfully");
        rs.setData(customer);

        return rs;
    }
}