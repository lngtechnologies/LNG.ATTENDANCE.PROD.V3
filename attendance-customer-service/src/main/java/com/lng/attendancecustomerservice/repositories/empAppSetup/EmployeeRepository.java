package com.lng.attendancecustomerservice.repositories.empAppSetup;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call getEmpDetailsByCustCodeAndEmpMobile(?1, ?2, ?3)", nativeQuery = true)
	Employee getEmployeeDetailsByCustomer_CustCodeAndEmployee_EmpMobile(String custCode, String empMobile, Integer custId);
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();
	
	@Query(value = "select * from tmemployee where refCustId = ?1 and empId = ?2 and empInService = true", nativeQuery = true)
	Employee getByEmpIdAndRefCustId(Integer custId, Integer empId);
	
	Employee getByEmpId(Integer empId);
	
	Employee getEmployeeByEmpId(Integer empId);
	
	// List<EmployeeAttendance> getByEmpAttendanceDatetimeAndrefEmpId(Date attndDate, Integer empId);
	
	@Query(value = "SELECT 	Min(empAttendanceInDatetime) AS DATE FROM ttempattendance WHERE empAttendanceInDatetime LIKE %?1% AND refEmpId = ?2", nativeQuery = true)
	Date getRecentInDateByAttndDateAndEmpId(String attndDate, Integer empId);
	
	@Query(value = "SELECT 	Min(empAttendanceOutDatetime) AS DATE FROM ttempattendance WHERE empAttendanceOutDatetime LIKE %?1% AND refEmpId = ?2", nativeQuery = true)
	Date getRecentOutDateByAttndDateAndEmpId(String attndDate, Integer empId);
	
	@Query(value = "SELECT 	MIN(empAttendanceDate) AS DATE FROM ttempattendance WHERE empAttendanceDate LIKE %?1% AND refEmpId = ?2", nativeQuery = true)
	Date getRecentAttndDate(String attndDate, Integer empId);
	
	@Query(value = "call getOutPermisableTimeByEmpIdAndCustId(?1,?2)", nativeQuery = true)
	String getOutPermissibleTimeByEmployee_EmpIdAndCustomer_CustId(Integer empId,Integer custId);
	
	@Query(value = "call GETREPORTS(?1,?2)", nativeQuery = true)
	List<Object[]> getReportsByLogin_LoginIdAndCustomer_custId(Integer loginId,Integer custId);
	
	@Query(value = "call GetReportsforAdmin(?1,?2)", nativeQuery = true)
	List<Object[]> getReportsForAdminByCustomer_custIdAndLogin_loginId(Integer custId,Integer loginId);
	
	/*
	 * @Query(value =
	 * "UPDATE tmemployee SET empAppSetupStatus=:empAppSetupStatus WHERE empId=:empId"
	 * , nativeQuery = true)
	 * 
	 * @Transactional Employee update(@Param("empId") Integer
	 * empId, @Param("empAppSetupStatus") String empAppSetupStatus);
	 */
}
