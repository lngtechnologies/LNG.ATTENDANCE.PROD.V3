package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeReportingTo;

public interface EmployeeReportingToRepository extends PagingAndSortingRepository<EmployeeReportingTo, Integer> {

	EmployeeReportingTo findByEmployee_EmpIdAndRefEmpReportingToIdAndEmpFromDate(Integer empId, Integer reportingToId, Date empFromDate);
	
	EmployeeReportingTo findByEmployee_EmpIdAndEmpFromDate(Integer empId, Date empFromDate);
	
	@Query(value = "SELECT MAX(ert.empFromDate) AS empFromDate,  ert.empReportingToId, ert.refEmpId, ert.refEmpReportingToId, ert.empToDate FROM ttempreportingto ert WHERE ert.refEmpId = ?1 AND ert.empToDate IS NULL", nativeQuery = true)
	EmployeeReportingTo findByEmpIdAndReportingToDateNull(Integer empId);
	
	@Query(value = "select  erpt.* from  ttempreportingto erpt where  erpt.refEmpId = ?1 and erpt.empToDate is null", nativeQuery = true)
	EmployeeReportingTo findEmployeeByEmployee_EmpId(Integer refEmpId);
	
	@Query(value = "call updateEmpReportingToTopManagerAfterDelete(?1)", nativeQuery = true)
	EmployeeReportingTo updateEmpReportingToTopManagerAfterDeleteByEmployee_EmpId(Integer refEmpId);
}
