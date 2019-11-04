package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeePic;

public interface EmployeePicRepository extends PagingAndSortingRepository<EmployeePic, Integer> {

	EmployeePic findByEmployee_EmpId(Integer empId);
}
