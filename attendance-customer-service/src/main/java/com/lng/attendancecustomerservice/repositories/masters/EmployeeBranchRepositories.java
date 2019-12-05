package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeBranch;

public interface EmployeeBranchRepositories extends PagingAndSortingRepository<EmployeeBranch, Integer> {

	EmployeeBranch findByEmployee_EmpId(Integer empId);
	
	@Query(value = "select max(ebr.branchFromDate) as branchFromDate, ebr.empBranchId, ebr.refEmpId,  ebr.refBranchId, ebr.branchToDate from ttempbranch ebr where ebr.refEmpId = ?1 and ebr.branchToDate is null", nativeQuery = true)
	EmployeeBranch findByEmpId(Integer empId);
	
	EmployeeBranch findByEmployee_EmpIdAndBranch_BrIdAndBranchFromDate(Integer empId, Integer brId, Date branchFromDate);
}
