package com.lng.attendancetabservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

	@Query(value = "SELECT tc.* FROM tmcustomer tc LEFT JOIN tmbranch tb ON tb.refCustomerId = tc.custId WHERE custCode = ?1 AND brCode = ?2 AND tc.custIsActive =TRUE AND tb.brIsActive = TRUE", nativeQuery = true)
	Customer findByCustomer_CustCodeAndBranch_BrCode(String custCode,String brCode);
	
	@Query(value = " CALL getCustBranchDetails(?1,?2)", nativeQuery = true)
	List<Object[]> findCustomerByCustomer_CustCodeAndBranch_BrCode(String custCode,String brCode);
	
	Customer findCustomerByCustId(Integer custId);
	
	Customer findCustomerByCustIdAndCustIsActive(Integer custId, Boolean custIsActive);
	
	@Query(value = "SELECT tc.* FROM tmcustomer tc WHERE tc.custCode = ?1", nativeQuery = true)
	Customer findByCustomer_CustCode(String custCode);
	
	@Query(value = "SELECT COUNT(*) AS cunt FROM tmcustomer WHERE custValidityEnd >= CURDATE() AND custId = ?1 AND custIsActive = TRUE", nativeQuery = true)
	int checkCustValidationByCustId(Integer custId);
	
}
