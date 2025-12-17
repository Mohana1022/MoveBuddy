package com.alpha.MoveBuddy.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

  
    List<Booking> findByCustomer_MobileNo(Long mobileNo);

    @Query("SELECT b FROM Booking b WHERE b.customer.mobileNo = :mobileNo AND LOWER(b.bookingStatus) = 'booked'")
    Booking findActiveBookingByCustomerId(@Param("mobileNo") Long mobileNo);

    Optional<Booking> findByVehicle_IdAndBookingDate(int vehicleId, LocalDate bookingDate);

    List<Booking> findByVehicle_Id(int vehicleId);

	List<Booking> findByCustomerMobileNo(long mobileNo);
}

