package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeDesignation;

public interface EmployeeDesignationRepository extends PagingAndSortingRepository<EmployeeDesignation, Integer> {

	EmployeeDesignation findByEmployee_EmpId(Integer empId);
	
	@Query(value = "SELECT MAX(edsg.empFromDate) AS empFromDate, edsg.empDesgnId, edsg.refEmpId, edsg.refDesignationId, edsg.empFromDate, edsg.empToDate FROM ttempdesignation edsg WHERE edsg.refEmpId = ?1 AND edsg.empToDate IS NULL", nativeQuery = true)
	EmployeeDesignation findByEmpId(Integer empId);
	
	EmployeeDesignation findByEmployee_EmpIdAndDesignation_DesignationIdAndEmpFromDate(Integer empId, Integer designationId, Date empFromDate);
	
	EmployeeDesignation findByEmployee_EmpIdAndEmpFromDate(Integer empId, Date empFromDate);
}
