package com.lng.attendancecustomerservice.repositories.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Customer;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Integer> {
	Customer findByCustId(Integer custId);
}
