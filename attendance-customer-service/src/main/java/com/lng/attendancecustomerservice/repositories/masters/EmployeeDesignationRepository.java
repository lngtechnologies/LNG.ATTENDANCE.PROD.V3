package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeDesignation;

public interface EmployeeDesignationRepository extends PagingAndSortingRepository<EmployeeDesignation, Integer> {

	EmployeeDesignation findByEmployee_EmpId(Integer empId);
}
