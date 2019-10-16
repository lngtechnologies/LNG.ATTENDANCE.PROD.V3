package com.lng.attendancecompanyservice.repositories.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Integer> {
	Customer findByCustId(Integer custId);
}
