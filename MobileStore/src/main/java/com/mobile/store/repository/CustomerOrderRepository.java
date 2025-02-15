package com.mobile.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.store.dto.Customer;
import com.mobile.store.dto.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer>{

	List<CustomerOrder> findByCustomerAndPaymentIdIsNotNull(Customer customer);

}
