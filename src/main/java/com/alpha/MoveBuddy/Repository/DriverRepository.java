package com.alpha.MoveBuddy.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

}
