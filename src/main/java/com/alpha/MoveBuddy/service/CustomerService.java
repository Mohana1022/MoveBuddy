package com.alpha.MoveBuddy.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository cr;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private BookingRepository br;
	
	@Autowired
	private VehicleRepository vr;

	@Value("${locationiq.api.key}")
	private String apiKey;
	
	ResponseStructure<String> rs = new ResponseStructure<>();

	public ResponseStructure<String> saveCustomer(RegisterCustomerDTO dto) {

	    Customer c = new Customer();
	    c.setName(dto.getName());
	    c.setAge(dto.getAge());
	    c.setGender(dto.getGender());
	    c.setMobileNo(dto.getMobileNo());
	    c.setEmailId(dto.getEmailId());
	    c.setCurrentLoc(dto.getLatitude());
	    c.setCurrentLoc(dto.getLongitude());

	    // Convert latitude & longitude to city
	    String url = "https://us1.locationiq.com/v1/reverse?key=" + apiKey +"&lat=" + dto.getLatitude() +"&lon=" + dto.getLongitude() +"&format=json";

	    Map response = restTemplate.getForObject(url, Map.class);
	    Map address = (Map) response.get("address");
	    String city = (String) address.get("city");
	    c.setCurrentLoc(city);

	    // STEP 1: Save customer first (to get ID)
	    Customer saved = cr.save(c);

	    // STEP 2: Create a default booking
	    Booking b = new Booking();
	    b.setCustomer(saved);
	    b.setSourceLoc(city);
	    b.setDestinationLoc("Not Assigned");
	    b.setDistanceTravelled(0);
	    b.setFare(0);
	    b.setEstimatedTime(0);
	    b.setBookingDate(0);

	    // STEP 3: Save the booking
	    br.save(b);

	    // STEP 4: Assign booking list to customer
	    saved.setBookinglist(List.of(b));
	    cr.save(saved);

	    rs.setStatuscode(HttpStatus.NOT_FOUND.value());
        rs.setMessage("Customer saved");
        rs.setData("saved");
        return rs;
	}

	@Transactional
	public ResponseStructure<String> deletecustomer(long mobileNo) {

	         
	Customer customer = cr.findByMobileNo(mobileNo).orElseThrow(() -> new CustomerNotFoundException());

	        
	         cr.deleteByMobileNo(mobileNo);

	         rs.setStatuscode(HttpStatus.FOUND.value());
	         rs.setMessage("Customer with given mobile number found and deleted");
	         rs.setData("Deleted Sucessfully");
	         return rs;
	     }
	
	public ResponseStructure<Customer> findCustomer(long mobileNo) {

	    Customer customer = cr.findByMobileNo(mobileNo)
	                          .orElseThrow(() -> new CustomerNotFoundException());

	    ResponseStructure<Customer> structure = new ResponseStructure<>();
	    structure.setStatuscode(HttpStatus.OK.value());
	    structure.setMessage("Customer found successfully!");
	    structure.setData(customer);

	    return structure;
	}
	
	public ResponseStructure<List<Vehicle>> getAvailableVehicles(long mobileNo) {
        ResponseStructure<List<Vehicle>> response = new ResponseStructure<>();

        // Find customer by mobile
        Customer customer = cr.findByMobileNo(mobileNo)
            .orElse(null);

        if (customer == null) {
            response.setStatuscode(404);
            response.setMessage("Customer not found with mobile: " + mobileNo);
            response.setData((List<Vehicle>) null);
            return response;
        }

        String currentCity = customer.getCurrentLoc();

        // Fetch vehicles in the same city with status "Available"
        List<Vehicle> vehicles = vr.findByCurrentCityAndAvailableStatus(currentCity, "Available");

        if (vehicles.isEmpty()) {
            response.setStatuscode(200);
            response.setMessage("No available vehicles in your city");
            response.setData(vehicles);
        } else {
            response.setStatuscode(200);
            response.setMessage("Available vehicles fetched successfully");
            response.setData(vehicles);
        }

        return response;
    }


	
	
	 }

