package com.alpha.MoveBuddy.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.DTO.LoginRequestDTO;
import com.alpha.MoveBuddy.DTO.RegisterCustomerDTO;
import com.alpha.MoveBuddy.DTO.RegisterDriverVehicleDTO;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.UsersRepo;
import com.alpha.MoveBuddy.Repository.VehicleRepository;
import com.alpha.MoveBuddy.Security.JwtUtil;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Users;
import com.alpha.MoveBuddy.entity.Vehicle;
import com.alpha.MoveBuddy.exception.MobileAlreadyRegisteredException;
import com.alpha.MoveBuddy.service.CustomerService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverrepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

    // ========================
    // CUSTOMER REGISTRATION
    // ========================
    @PostMapping("/register/customer")
    public ResponseEntity<ResponseStructure<String>> registerCustomer(
            @Valid @RequestBody RegisterCustomerDTO dto) {

        long mobileNo = dto.getMobileNo();

        if (usersRepository.existsByUsermobileNo(mobileNo)) {
            throw new MobileAlreadyRegisteredException();
        }

        Users user = new Users();
        user.setUsermobileNo(mobileNo);
        user.setUserPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("CUSTOMER");
        user = usersRepository.save(user);

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setAge(dto.getAge());
        customer.setGender(dto.getGender());
        customer.setMobileNo(mobileNo);
        customer.setEmailId(dto.getEmailId());
        customer.setCurrentLoc(
                customerService.getCityFromCoordinates(dto.getLatitude(), dto.getLongitude())
        );
        customer.setUsers(user);
        customerRepository.save(customer);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Customer registered successfully");
        rs.setData("SUCCESS");

        return ResponseEntity.ok(rs);
    }

    // ========================
    // DRIVER REGISTRATION
    // ========================
    @PostMapping("/register/driver")
    public ResponseEntity<ResponseStructure<String>> registerDriver(
            @Valid @RequestBody RegisterDriverVehicleDTO dto) {

        long mobileNo = dto.getMobileNo();

        if (usersRepository.existsByUsermobileNo(mobileNo)) {
            throw new MobileAlreadyRegisteredException();
        }

        Users users = new Users();
        users.setUsermobileNo(mobileNo);
        users.setUserPassword(passwordEncoder.encode(dto.getPassword()));
        users.setRole("DRIVER");
        users = usersRepository.save(users);

        Driver driver = new Driver();
        driver.setName(dto.getDriverName());
        driver.setAge(dto.getAge());
        driver.setGender(dto.getGender());
        driver.setMobileno(mobileNo);
        driver.setMailid(dto.getMailId());
        driver.setLicenseNo(dto.getLicenseNo());
        driver.setUpiid(dto.getUpiID());
        driver.setStatus("Available");
        driver.setUsers(users);
        driver = driverrepository.save(driver);

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setName(dto.getVehicleName());
        vehicle.setVehicleNo(dto.getVehicleNo());
        vehicle.setType(dto.getVehicleType());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getVehicleCapacity());
        vehicle.setCurrentCity(
                customerService.getCityFromCoordinates(dto.getLatitude(), dto.getLongitude())
        );
        vehicle.setAvailableStatus("Available");
        vehicle.setPricePerKM(dto.getPricePerKM());
        vehicle.setAvgSpeed(dto.getAverageSpeed());
        vehicleRepository.save(vehicle);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver and vehicle registered successfully");
        rs.setData("SUCCESS");

        return ResponseEntity.ok(rs);
    }

    // ========================
    // LOGIN
    // ========================
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(dto.getMobileNo()),
                        dto.getPassword()
                )
        );

        // Fetch user to get role for token
        Users user = usersRepository.findByUsermobileNo(dto.getMobileNo())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(String.valueOf(dto.getMobileNo()), user.getRole());

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", "Bearer " + token);
        tokenData.put("role", user.getRole());
        tokenData.put("mobileNo", dto.getMobileNo());

        ResponseStructure<Map<String, Object>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Login successful");
        rs.setData(tokenData);

        return ResponseEntity.ok(rs);
    }
}
