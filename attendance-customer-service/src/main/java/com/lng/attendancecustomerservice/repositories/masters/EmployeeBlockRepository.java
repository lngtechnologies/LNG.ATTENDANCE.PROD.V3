package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeBlock;

public interface EmployeeBlockRepository extends PagingAndSortingRepository<EmployeeBlock, Integer> {

	List<EmployeeBlock> findByEmployee_EmpId(Integer empId);
	
	EmployeeBlock findByEmpBlkId(Integer empBlkId);
}
