package com.alpha.MoveBuddy.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.MoveBuddy.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>{

	Optional<Payment> findByBooking_Id(int id);

}
