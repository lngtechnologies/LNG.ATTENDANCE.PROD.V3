package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeShift;

public interface EmployeeShiftRepository extends PagingAndSortingRepository<EmployeeShift, Integer> {

	EmployeeShift findByEmployee_EmpId(Integer empId);
	
	@Query(value = "SELECT MAX(esft.shiftFromDate) AS shiftFromDate, esft.empShiftId, esft.refEmpId, esft.refShiftId, esft.shiftFromDate, esft.shiftToDate FROM ttempshift esft WHERE esft.refEmpId = ?1 AND esft.shiftToDate IS NULL", nativeQuery = true)
	EmployeeShift findByEmpId(Integer empId);
	
	EmployeeShift findByEmployee_EmpIdAndShift_ShiftIdAndShiftFromDate(Integer empId, Integer shiftId, Date shiftFromDate);
	
	EmployeeShift findByEmployee_EmpIdAndShiftFromDate(Integer empId, Date shiftFromDate);
}
