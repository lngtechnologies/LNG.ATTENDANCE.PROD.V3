package com.lng.attendancecompanyservice.repositories.custOnboarding;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

	List<Customer> findAllCustomerByCustIsActive(Boolean custIsActive);
	
	Customer findCustomerByCustId(Integer custId);
	
	@Query(value = "CALL generateCustCode()", nativeQuery = true)
	String generateCustCode();
	
}
