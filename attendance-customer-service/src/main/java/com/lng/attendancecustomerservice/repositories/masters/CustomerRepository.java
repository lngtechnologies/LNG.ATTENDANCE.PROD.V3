package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

	Customer getByCustCode(String custCode);
	
	Customer findCustomerByCustIdAndCustIsActive(Integer custId, Boolean isActive);
	
	@Query(value = "SELECT COUNT(*) AS cunt FROM tmcustomer WHERE custValidityEnd > CURDATE() AND custId = ?1 AND custIsActive = TRUE", nativeQuery = true)
	int checkCustValidationByCustId(Integer custId);

}
