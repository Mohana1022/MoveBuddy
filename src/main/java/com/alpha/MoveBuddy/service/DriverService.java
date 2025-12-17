package com.alpha.MoveBuddy.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.DTO.BookingHistoryDto;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
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

    // ---------------- GET CITY NAME ----------------
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

    // ---------------- SAVE DRIVER ----------------
    public ResponseEntity<ResponseStructure<Driver>> saveDriverDTO(RegisterDriverVehicleDTO dto) {

        Driver d = new Driver();
        d.setLicenseNo(dto.getLicenseNo());
        d.setUpiid(dto.getUpiID());
        d.setName(dto.getDriverName());
        d.setAge(dto.getAge());
        d.setMobileno(dto.getMobileNo());
        d.setGender(dto.getGender());
        d.setMailid(dto.getMailId());

        Vehicle v = new Vehicle();
        v.setName(dto.getVehicleName());
        v.setVehicleNo(dto.getVehicleNo());
        v.setType(dto.getVehicleType());
        v.setModel(dto.getModel());
        v.setCapacity(dto.getVehicleCapacity());
        v.setPricePerKM(dto.getPricePerKM());
        v.setAvgSpeed(dto.getAverageSpeed());
        v.setCurrentCity(getCityName(dto.getLatitude(), dto.getLongitude()));

        v.setDriver(d);
        d.setVehicle(v);

        Driver savedDriver = dr.save(d);

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver saved successfully");
        rs.setData(savedDriver);

        return ResponseEntity.ok(rs);
    }

    // ---------------- FIND DRIVER ----------------
    public ResponseEntity<ResponseStructure<Driver>> findDriverByMobile(long mobileNo) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        ResponseStructure<Driver> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver found successfully");
        rs.setData(driver);

        return ResponseEntity.ok(rs);
    }

    // ---------------- COMPLETE RIDE ----------------
    public ResponseEntity<ResponseStructure<RideCompletionDTO>> completeRide(int bookingId, String paymentType) {

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

        ResponseStructure<RideCompletionDTO> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Ride completed successfully");
        rs.setData(dto);

        return ResponseEntity.ok(rs);
    }

    // ---------------- BOOKING HISTORY ----------------
    public ResponseEntity<ResponseStructure<BookingHistoryDto>> seeAllBookingHistory(long mobileNo) {

        Driver driver = dr.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        List<RideDetailsDTO> rideDetails = new ArrayList<>();
        double totalAmount = 0;

        for (Booking b : driver.getBookings()) {
            RideDetailsDTO dto = new RideDetailsDTO();
            dto.setFromLoc(b.getSourceLoc());
            dto.setToLoc(b.getDestinationLoc());
            dto.setDistance(b.getDistanceTravelled());
            dto.setFare(b.getFare());

            totalAmount += b.getFare();
            rideDetails.add(dto);
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

    // ---------------- CANCELLATION BY DRIVER ----------------
    public void cancelBooking(int driverId, LocalDate bookingDate) {

        Driver driver = dr.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Booking booking = br.findByVehicle_IdAndBookingDate(driverId, bookingDate)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("canceled by driver".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new RuntimeException("Booking already canceled");
        }

        int cancellationCount = 0;
        for (Booking b : br.findByVehicle_Id(driverId)) {
            if ("canceled by driver".equalsIgnoreCase(b.getBookingStatus())) {
                cancellationCount++;
            }
        }

        booking.setBookingStatus("canceled by driver");
        br.save(booking);

        if (cancellationCount + 1 >= 4) {
            driver.setStatus("blocked");
            dr.save(driver);
        }
    }

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

	public ResponseEntity<ResponseStructure<String>> updateDriverLocation(long mobileNo, String latitude,
			String longitude) {
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
}
 