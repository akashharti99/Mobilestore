package com.mobile.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.store.dto.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	Customer findByEmail(String email);

	Optional<Customer> findByOtp(int otp);

}
