package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.EmployeeType;

@Repository
public interface EmployeeTypeRepository extends PagingAndSortingRepository<EmployeeType, Integer> {

	EmployeeType findEmployeeTypeByEmpTypeId(Integer empTypeId);
	
	List<EmployeeType> findAll();
}
