package com.lng.attendancecompanyservice.repositories.custOnboarding;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.dto.customer.CustomerDto;
import com.lng.dto.customer.CustomerDtoTwo;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

	List<Customer> findAllCustomerByCustIsActive(Boolean custIsActive);
	
	Customer findCustomerByCustId(Integer custId);
	
	@Query(value = "CALL generateCustCode()", nativeQuery = true)
	String generateCustCode();
	
	@Query(value = "CALL searchCustomerByNameAndCode(?1)", nativeQuery = true)
	List<Customer> searchAllCustomerByNameOrCode(String cust);
	
	List<Customer> findCustomerByCustEmail(String custEmail);
	
	List<Customer> findCustomerByCustMobile(String custMobile);
	
	List<Customer> findCustomerByIndustryType_IndustryId(Integer industryId);
	
	Customer findCustomerByCustCode(String custCode);
	
	List<Customer> findByState_StateId(Integer stateId);
	
}
