package com.lng.attendancecompanyservice.repositories.masters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Branch;

public interface BranchRepository extends PagingAndSortingRepository<Branch, Integer> {

	@Query(value = "CALL generateBranchCodeForCustomer(?1)", nativeQuery = true)
	String generateBranchForCustomer(Integer custId);
	
	Branch findBranchByBrId(Integer brId);
}
