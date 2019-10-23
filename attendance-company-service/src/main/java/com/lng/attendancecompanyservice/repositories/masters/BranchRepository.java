package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Branch;

public interface BranchRepository extends PagingAndSortingRepository<Branch, Integer> {

	@Query(value = "CALL generateBranchCodeForCustomer(?1)", nativeQuery = true)
	String generateBranchForCustomer(Integer custId);
	
	//Branch findBranchByBrId(Integer brId);
	
	List<Branch> findAllBranchByBrIsActive(Boolean brIsActive);
	Branch findBranchByBrId(Integer brId);
	
	@Query(value = "CALL  CheckBranchIsExistForCustomer(?1, ?2);",nativeQuery = true)
	int  findByRefCustomerIdAndBrName(Integer refCustomerId,String brName);
	
	Branch findBranchBybrNameAndCustomer_custId(String brName, int custId);
}
