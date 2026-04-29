package com.alpha.MoveBuddy.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RideCompletionDTO;
import com.alpha.MoveBuddy.DTO.RideDetailsDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.PaymentRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Payment;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.DriverNotFoundException;

import java.util.Map;

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

    // ---- GET CITY NAME FROM COORDINATES ----
    public String getCityName(String lat, String lon) {
        String url = "https://us1.locationiq.com/v1/reverse?key=" + apiKey +
                "&lat=" + lat + "&lon=" + lon + "&format=json";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> address = (Map<String, Object>) response.get("address");

        if (address.get("city") != null) return address.get("city").toString();
        if (address.get("town") != null) return address.get("town").toString();
        if (address.get("village") != null) return address.get("village").toString();
        return "Unknown";
    }

    // ---- FIND DRIVER ----
    public ResponseEntity<ResponseStructure<Driver>> findDriverByMobile(long mobileNo) {
        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver found successfully");
        rs.setData(driver);

        return ResponseEntity.ok(rs);
    }

    // ---- TOGGLE AVAILABILITY ----
    public ResponseEntity<ResponseStructure<String>> toggleAvailability(long mobileNo) {
        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        String currentStatus = driver.getStatus();
        String newStatus = "Available".equalsIgnoreCase(currentStatus) ? "Offline" : "Available";

        driver.setStatus(newStatus);
        dr.save(driver);

        // Also update the vehicle availability
        if (driver.getVehicle() != null) {
            driver.getVehicle().setAvailableStatus(newStatus);
            vr.save(driver.getVehicle());
        }

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Availability toggled");
        rs.setData("Status changed to: " + newStatus);

        return ResponseEntity.ok(rs);
    }

    // ---- GET INCOMING RIDES (PENDING) ----
    public ResponseEntity<ResponseStructure<List<Booking>>> getIncomingRides(long mobileNo) {
        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        if (driver.getVehicle() == null) {
            throw new RuntimeException("Vehicle not associated with driver");
        }

        String driverCity = driver.getVehicle().getCurrentCity();

        // Find all PENDING bookings in the driver's city
        List<Booking> allBookings = br.findAll();
        List<Booking> incomingRides = allBookings.stream()
                .filter(b -> {
                    if (b.getVehicle() == null) return false;
                    
                    // Always show if it belongs to this driver
                    if (b.getVehicle().getDriver() != null && 
                        b.getVehicle().getDriver().getMobileno().equals(mobileNo)) {
                        return !"COMPLETED".equalsIgnoreCase(b.getBookingStatus());
                    }
                    
                    // Otherwise show open rides in the same city
                    return ("PENDING".equalsIgnoreCase(b.getBookingStatus()) || 
                            "booked".equalsIgnoreCase(b.getBookingStatus())) &&
                           driverCity.equalsIgnoreCase(b.getVehicle().getCurrentCity());
                })
                .toList();

        ResponseStructure<List<Booking>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Incoming rides fetched for city: " + driverCity);
        rs.setData(incomingRides);

        return ResponseEntity.ok(rs);
    }

    // ---- COMPLETE RIDE ----
    public ResponseEntity<ResponseStructure<RideCompletionDTO>> completeRide(int bookingId, String paymentType) {
        Booking booking = br.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.isOtpVerified()) {
            throw new RuntimeException("OTP not verified. Cannot complete ride.");
        }

        booking.setBookingStatus("COMPLETED");
        booking.setPaymentStatus("PAID");

        Customer customer = booking.getCustomer();
        customer.setBookingflag(false);

        Vehicle vehicle = booking.getVehicle();
        vehicle.setAvailableStatus("Available");

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

        ResponseStructure<RideCompletionDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Ride completed successfully");
        rs.setData(dto);

        return ResponseEntity.ok(rs);
    }

    // ---- BOOKING HISTORY ----
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(long mobileNo) {
        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        List<RideDetailsDTO> rideDetails = new ArrayList<>();
        double totalAmount = 0;

        for (Booking b : driver.getBookings()) {
            RideDetailsDTO dto = new RideDetailsDTO();
            dto.setId(b.getId());
            dto.setFromLoc(b.getSourceLoc());
            dto.setToLoc(b.getDestinationLoc());
            dto.setDistance(b.getDistanceTravelled());
            dto.setFare(b.getFare());
            dto.setStatus(b.getBookingStatus());
            dto.setBookingDate(b.getBookingDate());
            dto.setDriverName(driver.getName());
            if (b.getCustomer() != null) {
                dto.setCustomerName(b.getCustomer().getName());
            }
            
            rideDetails.add(dto);
            if ("COMPLETED".equalsIgnoreCase(b.getBookingStatus())) {
                totalAmount += b.getFare();
            }
        }

        BookingHistoryDto history = new BookingHistoryDto();
        history.setHistory(rideDetails);
        history.setTotalAmount(totalAmount);

        ResponseStructure<BookingHistoryDto> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Booking history fetched successfully");
        rs.setData(history);

        return ResponseEntity.ok(rs);
    }

    // ---- DRIVER CANCELLATION ----
    @org.springframework.transaction.annotation.Transactional
    public void cancelBooking(int driverId, LocalDate bookingDate) {
        Driver driver = dr.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Booking booking = br.findByVehicle_IdAndBookingDate(driverId, bookingDate)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("canceled by driver".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new RuntimeException("Booking already canceled");
        }

        // Reset customer flag
        if (booking.getCustomer() != null) {
            booking.getCustomer().setBookingflag(false);
            cr.save(booking.getCustomer());
        }

        // Reset vehicle availability
        if (booking.getVehicle() != null) {
            booking.getVehicle().setAvailableStatus("Available");
            vr.save(booking.getVehicle());
        }

        long cancellationCount = br.findByVehicle_Id(driverId).stream()
                .filter(b -> "canceled by driver".equalsIgnoreCase(b.getBookingStatus()))
                .count();

        booking.setBookingStatus("canceled by driver");
        br.save(booking);

        if (cancellationCount + 1 >= 4) {
            driver.setStatus("blocked");
            dr.save(driver);
        }
    }

    // ---- DELETE DRIVER ----
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

    // ---- UPDATE DRIVER LOCATION ----
    public ResponseEntity<ResponseStructure<String>> updateDriverLocation(
            long mobileNo, String latitude, String longitude) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not found"));

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

    // ---- OTP VALIDATION ----
    public ResponseEntity<ResponseStructure<String>> validateRideOtp(int bookingId, String enteredOtp) {
        Booking booking = br.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getRideOtp().equals(enteredOtp)) {
            throw new RuntimeException("Invalid OTP. Ride cannot be started.");
        }

        booking.setOtpVerified(true);
        booking.setBookingStatus("IN_PROGRESS");
        br.save(booking);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("OTP verified. Ride is now in progress.");
        rs.setData("OTP VERIFIED");

        return ResponseEntity.ok(rs);
    }

    public ResponseStructure<Driver> updateDriver(Driver updated) {
        Driver existing = dr.findByMobileno(updated.getMobileno())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        existing.setName(updated.getName());
        existing.setMailid(updated.getMailid());
        existing.setAge(updated.getAge());
        existing.setGender(updated.getGender());

        Driver saved = dr.save(existing);

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Profile updated");
        rs.setData(saved);
        return rs;
    }
}
