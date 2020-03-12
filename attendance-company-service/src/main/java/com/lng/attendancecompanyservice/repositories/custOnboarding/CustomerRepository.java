package com.lng.attendancecompanyservice.repositories.custOnboarding;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
	
	@Query(value = "SELECT * FROM tmcustomer WHERE custIsActive = TRUE ORDER BY custName ASC", nativeQuery = true)
	List<Customer> findAllCustomerByCustIsActive();
	
	List<Customer> getAllCustomerByCustIsActive(Boolean isActive);
	
	Customer findCustomerByCustId(Integer custId);
	
	@Query(value = "SELECT custNoOfBranch FROM tmCustomer  WHERE custId = ?1", nativeQuery = true)
	int findCustomerByCustNoOfBranch(Integer custNoOfBranch);
	
	@Query(value = "CALL generateCustCode()", nativeQuery = true)
	String generateCustCode();
	
	@Query(value = "CALL searchCustomerByNameAndCode(?1)", nativeQuery = true)
	List<Customer> searchAllCustomerByNameOrCode(String cust);
	
	List<Customer> findCustomerByCustEmailAndCustIsActive(String custEmail, Boolean isActive);
	
	List<Customer> findCustomerByCustName(String custName);
	
	List<Customer> findCustomerByCustMobileAndCustIsActive(String custMobile, Boolean isActive);
	
	List<Customer> findCustomerByIndustryType_IndustryId(Integer industryId);
	
	Customer findCustomerByCustCode(String custCode);
	
	List<Customer> findByState_StateId(Integer stateId);
	
	Customer getCustomerByCustMobileAndCustIsActive(String custMobile, Boolean isActive);
	
	Customer getCustomerByCustEmailAndCustIsActive(String custEmail, Boolean isActive);
	
	@Query(value = "SELECT * FROM tmcustomer WHERE custValidityEnd > CURDATE() AND custIsActive = TRUE", nativeQuery = true)
	List<Customer> getAllActiveCustomers();
	
	@Query(value = "SELECT * FROM tmcustomer WHERE custValidityEnd < CURDATE() AND custIsActive = TRUE", nativeQuery = true)
	List<Customer> getAllExpiredCustomers();
	
}
