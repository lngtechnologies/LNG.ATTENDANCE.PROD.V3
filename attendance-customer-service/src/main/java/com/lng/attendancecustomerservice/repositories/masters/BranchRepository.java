package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Branch;
@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch,Integer> {
	
	Branch findBranchByBrId(Integer brId);
	
	Branch findBranchByCustomer_custId(Integer custId);
}
