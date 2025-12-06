package com.alpha.MoveBuddy.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{

	
}
