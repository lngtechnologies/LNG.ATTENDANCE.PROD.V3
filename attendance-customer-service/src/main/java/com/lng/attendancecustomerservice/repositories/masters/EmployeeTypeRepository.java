package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeType;

public interface EmployeeTypeRepository extends PagingAndSortingRepository<EmployeeType, Integer> {

	EmployeeType findEmployeeTypeByEmpTypeId(Integer empTypeId);
}
