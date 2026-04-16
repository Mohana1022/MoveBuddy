package com.alpha.MoveBuddy.controller;

import com.alpha.MoveBuddy.DTO.RatingDTO;
import com.alpha.MoveBuddy.ResponseStructure;
import com.alpha.MoveBuddy.entity.Rating;
import com.alpha.MoveBuddy.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * POST /customer/rating/submit?customerMobile=9876543210
     * Body: { bookingId, stars, comment }
     */
    @PostMapping("/submit")
    public ResponseEntity<ResponseStructure<Rating>> submitRating(
            @RequestParam long customerMobile,
            @Valid @RequestBody RatingDTO dto) {
        return ratingService.submitRating(customerMobile, dto);
    }

    /**
     * GET /customer/rating/driver/{driverId}
     * Returns avg rating + total count for a driver.
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ResponseStructure<Map<String, Object>>> getDriverRating(
            @PathVariable int driverId) {
        return ratingService.getDriverRating(driverId);
    }
}
