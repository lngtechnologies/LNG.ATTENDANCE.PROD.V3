package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.EmpLeave;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;

@Repository
public interface EmployeeLeaveRepository extends PagingAndSortingRepository<EmployeeLeave, Integer> {

	@Query(value = "call getEmpDetailsByBrIdForEmpLeave(?1)", nativeQuery = true)
	List<Object[]> findEmpDataByBrId(Integer brId);
	
	@Query(value = "call calNoDaysBetweenTwoDates(?1, ?2)", nativeQuery = true)
	int getNoOfDaysCount(Date fromDate, Date toDate );
	
	@Query(value = "call getEmpLeaveByLoginIdAndCustId(?1, ?2)", nativeQuery = true)
	List<Object[]> getEmpLeaveByLoginIdAndCustId(Integer loginId, Integer custId);
	
	EmployeeLeave findByEmpLeaveId(Integer empLeaveId);
	
	@Query(value = "call getEmpLeaveAppByLoginIdAndCustId(?1, ?2)", nativeQuery = true)
	List<Object[]> getEmpLeaveAppByLoginIdAndCustId(Integer loginId, Integer custId);
	
	@Query(value = "call getRecentEmpLeaveByEmpId(?1)", nativeQuery = true)
	List<Object[]> findByEmployee_EmpId(Integer empId);
	
	@Query(value = "call getEmpLeaveByLoginIdAndCustIdAndEmpId(?1, ?2, ?3,?4,?5)", nativeQuery = true)
	List<Object[]> getEmpLeaveByLoginIdAndCustIdAndEmpId(Integer loginId, Integer custId,Integer empId,Date empLeaveFrom,Date empLeaveTo);
	
	List<EmployeeLeave> getEmployeeLeaveByEmployee_EmpIdAndEmpLeaveFrom(Integer empId, Date empLeaveFrom);
	
	@Query(value = "call getEmpLeaveAlreadyApplied(?1, ?2, ?3)", nativeQuery = true)
	int getEmpLeaveAlreadyApplied(Date empLeaveFrom, Date empLeaveTo, Integer empId);
	
	
}
