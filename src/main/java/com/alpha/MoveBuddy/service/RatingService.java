package com.alpha.MoveBuddy.service;

import com.alpha.MoveBuddy.DTO.RatingDTO;
import com.alpha.MoveBuddy.Repository.BookingRepository;
import com.alpha.MoveBuddy.Repository.CustomerRepository;
import com.alpha.MoveBuddy.Repository.RatingRepository;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Rating;
import com.alpha.MoveBuddy.Repository.DriverRepository;
import com.alpha.MoveBuddy.exception.BookingNotFoundException;
import com.alpha.MoveBuddy.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Submit a rating for a completed ride.
     * A customer can only rate once per booking.
     */
    public ResponseEntity<ResponseStructure<Rating>> submitRating(long customerMobile, RatingDTO dto) {

        // 1. Find the booking
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(BookingNotFoundException::new);

        // 2. Verify the booking belongs to this customer
        if (booking.getCustomer() == null ||
                booking.getCustomer().getMobileNo() != customerMobile) {
            throw new RuntimeException("Booking does not belong to this customer");
        }

        // 3. Only allow rating on COMPLETED rides
        if (!"COMPLETED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new RuntimeException("You can only rate a completed ride");
        }

        // 4. Prevent duplicate rating
        if (ratingRepository.existsByBooking_Id(dto.getBookingId())) {
            throw new RuntimeException("You have already rated this ride");
        }

        // 5. Build and save Rating (use saveAndFlush to ensure it's in the DB for the AVG query)
        Rating rating = new Rating();
        rating.setBooking(booking);
        rating.setCustomer(booking.getCustomer());
        rating.setDriver(booking.getVehicle().getDriver());
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());

        Rating saved = ratingRepository.saveAndFlush(rating);

        // 6. Sync Driver's aggregate rating
        Driver driver = saved.getDriver();
        if (driver != null) {
            Double avg = ratingRepository.findAverageRatingByDriverId(driver.getId());
            double finalAvg = (avg != null) ? avg : (double) dto.getStars();
            
            // Round to 1 decimal place (e.g. 4.666 -> 4.7)
            double roundedAvg = Math.round(finalAvg * 10.0) / 10.0;
            driver.setRating(roundedAvg);
            driverRepository.save(driver);
        }

        ResponseStructure<Rating> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Rating submitted successfully");
        rs.setData(saved);

        return ResponseEntity.ok(rs);
    }

    /**
     * Get average rating for a driver.
     */
    public ResponseEntity<ResponseStructure<Map<String, Object>>> getDriverRating(int driverId) {

        Double avgRating = ratingRepository.findAverageRatingByDriverId(driverId);
        long totalRatings = ratingRepository.findByDriver_Id(driverId).size();

        Map<String, Object> data = Map.of(
                "driverId", driverId,
                "averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0,
                "totalRatings", totalRatings
        );

        ResponseStructure<Map<String, Object>> rs = new ResponseStructure<>();
        rs.setStatuscode(HttpStatus.OK.value());
        rs.setMessage("Driver rating fetched successfully");
        rs.setData(data);

        return ResponseEntity.ok(rs);
    }
}
