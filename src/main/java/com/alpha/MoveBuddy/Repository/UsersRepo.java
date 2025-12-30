package com.alpha.MoveBuddy.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alpha.MoveBuddy.entity.Users;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsermobileNo(long usermobileNo);

    boolean existsByUsermobileNo(long usermobileNo);
}
