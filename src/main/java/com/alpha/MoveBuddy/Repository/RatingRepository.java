package com.alpha.MoveBuddy.Repository;

import com.alpha.MoveBuddy.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByDriver_Id(int driverId);

    Optional<Rating> findByBooking_Id(int bookingId);

    boolean existsByBooking_Id(int bookingId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.driver.id = :driverId")
    Double findAverageRatingByDriverId(@Param("driverId") int driverId);
}
