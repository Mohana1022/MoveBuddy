package com.alpha.MoveBuddy.controller;

import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.Repository.UsersRepo;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Users;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;
import com.alpha.MoveBuddy.exception.DriverNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ========================
    // DASHBOARD STATS
    // ========================

    /**
     * GET /admin/dashboard
     * Returns aggregate stats: total users, rides, customers, drivers.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", usersRepo.count());
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalDrivers", driverRepository.count());
        stats.put("totalRides", bookingRepository.count());
        stats.put("completedRides", bookingRepository.findAll().stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getBookingStatus())).count());
        stats.put("activeRides", bookingRepository.findAll().stream()
                .filter(b -> "booked".equalsIgnoreCase(b.getBookingStatus())).count());

        ResponseStructure<Map<String, Object>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Dashboard stats fetched");
        rs.setData(stats);

        return ResponseEntity.ok(rs);
    }

    // ========================
    // USER MANAGEMENT
    // ========================

    /** GET /admin/users – List all registered users */
    @GetMapping("/users")
    public ResponseEntity<ResponseStructure<List<Users>>> getAllUsers() {
        List<Users> users = usersRepo.findAll();

        ResponseStructure<List<Users>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("All users fetched");
        rs.setData(users);

        return ResponseEntity.ok(rs);
    }

    /** GET /admin/customers – List all customers */
    @GetMapping("/customers")
    public ResponseEntity<ResponseStructure<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        ResponseStructure<List<Customer>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("All customers fetched");
        rs.setData(customers);

        return ResponseEntity.ok(rs);
    }

    /** GET /admin/drivers – List all drivers */
    @GetMapping("/drivers")
    public ResponseEntity<ResponseStructure<List<Driver>>> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();

        ResponseStructure<List<Driver>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("All drivers fetched");
        rs.setData(drivers);

        return ResponseEntity.ok(rs);
    }

    /** GET /admin/rides – List all rides/bookings */
    @GetMapping("/rides")
    public ResponseEntity<ResponseStructure<List<Booking>>> getAllRides() {
        List<Booking> bookings = bookingRepository.findAll();

        ResponseStructure<List<Booking>> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("All rides fetched");
        rs.setData(bookings);

        return ResponseEntity.ok(rs);
    }

    // ========================
    // SUSPEND / RESTORE
    // ========================

    /**
     * PUT /admin/suspend/driver/{mobileNo}
     * Suspends a driver by setting their status to "SUSPENDED".
     */
    @PutMapping("/suspend/driver/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> suspendDriver(@PathVariable long mobileNo) {
        Driver driver = driverRepository.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        driver.setStatus("SUSPENDED");
        driverRepository.save(driver);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver suspended successfully");
        rs.setData("SUSPENDED");

        return ResponseEntity.ok(rs);
    }

    /**
     * PUT /admin/restore/driver/{mobileNo}
     * Restores a suspended driver.
     */
    @PutMapping("/restore/driver/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> restoreDriver(@PathVariable long mobileNo) {
        Driver driver = driverRepository.findByMobileno(mobileNo)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        driver.setStatus("Available");
        driverRepository.save(driver);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(200);
        rs.setMessage("Driver account restored successfully");
        rs.setData("RESTORED");

        return ResponseEntity.ok(rs);
    }

    /**
     * DELETE /admin/customer/{mobileNo}
     * Removes a customer account.
     */
    @DeleteMapping("/customer/{mobileNo}")
    public ResponseEntity<ResponseStructure<String>> deleteCustomer(@PathVariable long mobileNo) {
        Customer customer = customerRepository.findByMobileNo(mobileNo)
                .orElseThrow(CustomerNotFoundException::new);

        customerRepository.delete(customer);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Customer account deleted");
        rs.setData("DELETED");

        return ResponseEntity.ok(rs);
    }
}
