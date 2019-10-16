package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeType;

@Repository
public interface EmployeeTypeRepository extends PagingAndSortingRepository<EmployeeType, Integer>{

	EmployeeType findEmployeeTypeByEmpType(String empType);
	
	List<EmployeeType> findAll();
	
	EmployeeType findEmployeeTypeByEmpTypeIdAndEmpType(Integer empTypeId, String empType);
	
	EmployeeType findEmployeeTypeByEmpTypeId(Integer empTypeId);
	
	@Query(value = "CALL checkEmployeeTypeExistOrNot(?1)", nativeQuery = true)	
	EmployeeType checkEmployeeTypeExistOrNot(Integer empTypeId);

}
