package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancetabservice.entity.EmployeePic;

public interface EmployeePicRepository extends PagingAndSortingRepository<EmployeePic, Integer> {

	EmployeePic findByEmployee_EmpId(Integer empId);
}
