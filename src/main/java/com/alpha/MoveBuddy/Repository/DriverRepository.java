package com.alpha.MoveBuddy.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Booking;
import com.alpha.MoveBuddy.entity.Customer;
import com.alpha.MoveBuddy.entity.Driver;
import com.alpha.MoveBuddy.entity.Payment;
import com.alpha.MoveBuddy.entity.Vehicle;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
	Optional<Driver> findByMobileno(long mobileno);
	

//    void deleteByMobileno(long mobileno);
//
//	void save(Customer c);
//
//	void save(Vehicle v);
//
//	void save(Booking booking);
//
//	void save(Payment p);
	@Query("SELECT d FROM Driver d LEFT JOIN FETCH d.vehicle WHERE d.mobileno = :mobileNo")
    Optional<Driver> findByMobilenoWithVehicle(@Param("mobileNo") long mobileNo);
}