package com.lng.attendancecustomerservice.repositories.empAppSetup;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call getEmpDetailsByCustCodeAndEmpMobile(?1, ?2, ?3)", nativeQuery = true)
	Employee getEmployeeDetailsByCustomer_CustCodeAndEmployee_EmpMobile(String custCode, String empMobile, Integer custId);
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();
	
	@Query(value = "select * from tmemployee where refCustId = ?1 and empId = ?2", nativeQuery = true)
	Employee getByEmpIdAndRefCustId(Integer custId, Integer empId);
	
	Employee getByEmpId(Integer empId);
	
	Employee getEmployeeByEmpId(Integer empId);
	
	// List<EmployeeAttendance> getByEmpAttendanceDatetimeAndrefEmpId(Date attndDate, Integer empId);
	
	@Query(value = "SELECT 	MIN(empAttendanceDatetime) AS DATE FROM	ttempattendance WHERE empAttendanceDatetime LIKE %?1% AND refEmpId = ?2", nativeQuery = true)
	Date getRecentDateByAttndDateAndEmpId(String attndDate, Integer empId);
	
	/*
	 * @Query(value =
	 * "UPDATE tmemployee SET empAppSetupStatus=:empAppSetupStatus WHERE empId=:empId"
	 * , nativeQuery = true)
	 * 
	 * @Transactional Employee update(@Param("empId") Integer
	 * empId, @Param("empAppSetupStatus") String empAppSetupStatus);
	 */
}
