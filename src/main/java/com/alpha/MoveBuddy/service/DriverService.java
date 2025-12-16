package com.alpha.MoveBuddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.DTO.RideCompletionDTO;
import com.alpha.MoveBuddy.DTO.RideDetailsDTO;
import com.alpha.MoveBuddy.DTO.UpiDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.PaymentRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Payment;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.DriverNotFoundException;

@Service
public class DriverService {

    @Autowired
    private DriverRepository dr;

    @Autowired
    private VehicleRepository vr;
    
    @Autowired
    private CustomerRepository cr;
    
    @Autowired
    private BookingRepository br;
    @Autowired
    private PaymentRepository pr;

    @Value("${locationiq.api.key}")
    private String apiKey;


    // GET CITY NAME (same – no change)
    
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



    // SAVE DRIVER + VEHICLE  (returns ResponseStructure<Driver>)
    
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


    // FIND DRIVER BY MOBILE  (returns ResponseStructure<Driver>)
    
    public ResponseEntity<ResponseStructure<Driver>> findDriverByMobile(long mobileNo) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with mobile: " + mobileNo));

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver found successfully");
        rs.setData(driver);

        return ResponseEntity.ok(rs);
    }



    // DELETE DRIVER  (returns ResponseStructure<String>)
    
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



    // UPDATE DRIVER LOCATION (returns ResponseStructure<String>)
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

//CompletionRide
    
    public ResponseEntity<ResponseStructure<RideCompletionDTO>>
    completeRide(int bookingId, String paymentType) {

        if (paymentType.equalsIgnoreCase("CASH")) {
            return cashPayment(bookingId);
        } 
        else if (paymentType.equalsIgnoreCase("UPI")) {
            return upiPayment(bookingId);
        } 
        else {
            throw new RuntimeException("Invalid payment type");
        }
    }

    
    // CASH PAYMENT
    
    private ResponseEntity<ResponseStructure<RideCompletionDTO>>
    cashPayment(int bookingId) {

        RideCompletionDTO dto = completeRideCommonLogic(bookingId, "CASH");

        ResponseStructure<RideCompletionDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Cash payment successful");
        rs.setData(dto);

        return ResponseEntity.ok(rs);
    }

    
    // UPI PAYMENT
    
    private ResponseEntity<ResponseStructure<RideCompletionDTO>>
    upiPayment(int bookingId) {

        RideCompletionDTO dto = completeRideCommonLogic(bookingId, "UPI");

        ResponseStructure<RideCompletionDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("UPI payment successful");
        rs.setData(dto);

        return ResponseEntity.ok(rs);
    }

    // COMMON RIDE COMPLETION LOGIC
    
    private RideCompletionDTO
    completeRideCommonLogic(int bookingId, String paymentType) {

        Booking booking = br.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        
        booking.setBookingStatus("COMPLETED");
        booking.setPaymentStatus("PAID");

        Customer customer = booking.getCustomer();
        customer.setBookingflag(false);

        Vehicle vehicle = booking.getVehicle();
        vehicle.setAvailableStatus("AVAILABLE");

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setCustomer(customer);
        payment.setVehicle(vehicle);
        payment.setAmount(booking.getFare());
        payment.setPaymenttype(paymentType);

        br.save(booking);
        cr.save(customer);
        vr.save(vehicle);
        pr.save(payment);

        RideCompletionDTO dto = new RideCompletionDTO();
        dto.setBooking(booking);
        dto.setCustomer(customer);
        dto.setVehicle(vehicle);
        dto.setPayment(payment);

        return dto;
    }



    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(long mobileNo) {

    	Driver d = dr.findByMobileno(mobileNo)
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                "Driver not found with mobile: " + mobileNo));

        List<Booking> blist = d.getBookings();
        List<RideDetailsDTO> rideDetailsdto = new ArrayList<>();
        double totalAmount = 0;

        for (Booking b : blist) {
            RideDetailsDTO rdto = new RideDetailsDTO();
            rdto.setFromLoc(b.getSourceLoc());
            rdto.setToLoc(b.getDestinationLoc());
            rdto.setDistance(b.getDistanceTravelled());
            rdto.setFare(b.getFare());

            totalAmount += b.getFare();
            rideDetailsdto.add(rdto); 
        }

        BookingHistoryDto bookingHistorydto = new BookingHistoryDto();
        bookingHistorydto.setHistory(rideDetailsdto);
        bookingHistorydto.setTotalAmount(totalAmount);

        ResponseStructure<BookingHistoryDto> responsestructure = new ResponseStructure<>();
        responsestructure.setStatuscode(200);
        responsestructure.setMessage("Booking history fetched successfully");
        responsestructure.setData(bookingHistorydto);

        return ResponseEntity.ok(responsestructure);
    }

   

	}

    
