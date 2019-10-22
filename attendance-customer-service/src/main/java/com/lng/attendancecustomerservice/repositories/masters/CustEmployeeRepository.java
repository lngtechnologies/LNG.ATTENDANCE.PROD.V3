package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.Employee;

public interface CustEmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {

	
}
