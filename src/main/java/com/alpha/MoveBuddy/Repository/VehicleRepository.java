package com.alpha.MoveBuddy.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

	 List<Vehicle> findByCurrentCityAndAvailableStatus(String currentCity, String availableStatus);

}
