package com.lng.attendancetabservice.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Branch;
@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch,Integer> {
	
	@Query(value = " SELECT tb.* FROM tmbranch tb  LEFT JOIN tmcustomer tc ON tc.custId =tb.refCustomerId WHERE brCode = ?1 AND tc.custId =?2", nativeQuery = true)
	Branch findByBranch_BrCodeAndCustomer_CustId(String brCode,Integer CustId);
	
	Branch findByBrId(Integer brId);
	
	Branch findByBrIdAndBrIsActive(Integer brId, Boolean brIsActive);
	
	@Query(value = "SELECT COUNT(*) AS cunt FROM tmbranch WHERE brValidityEnd >= CURDATE() AND brId = ?1 AND brIsActive = TRUE", nativeQuery = true)
	int checkBranchValidity(Integer brId);
	

}
