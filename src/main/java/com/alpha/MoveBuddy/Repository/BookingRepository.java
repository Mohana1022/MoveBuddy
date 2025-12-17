package com.alpha.MoveBuddy.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{

	List<Booking> findByCustomerMobileNo(Long mobileNo);

	@Query("SELECT b FROM Booking b WHERE b.customer.mobileNo = :mobileNo AND LOWER(b.bookingStatus) = 'booked'")
	Booking findActiveBookingByCustomerId(@Param("mobileNo") Long mobileNo);

	List<Booking> findByVehicleId(int id);

}
