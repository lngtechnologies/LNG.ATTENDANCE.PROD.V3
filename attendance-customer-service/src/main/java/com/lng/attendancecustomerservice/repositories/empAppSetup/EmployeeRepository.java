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
	
	@Query(value = "call getAllShiftDEtailsByCustIdAndEmpId(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getShiftDetailsByDatesAndCustomer_CustIdAndEmployee_EmpId(Date dates,Integer custId,Integer empId);
	
	@Query(value = "call getEmployeeDetailsWithShiftDetailsForLateComers(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getLateComersDetailsByDatesAndCustomer_CustIdAndEmployee_EmpId(Date dates,Integer custId,Integer empId);
	
	@Query(value = "call getShiftDetailsForEarlyLeavers(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getShiftDetailsForEarlyLeaverByDatesAndCustomer_CustIdAndEmployee_EmpId(Date dates,Integer custId,Integer empId);
	
	@Query(value = "call getShiftDetailsWithEmpDetailsForEarlyLeavers(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getEmployeeDetailsEarlyLeaverByDatesAndCustomer_CustIdAndEmployee_EmpId(Date dates,Integer custId,Integer empId);
	
	@Query(value = "call M_getShiftWiseCountOfAbsentByCustIdAndEmpId(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getShiftDetailsForAbsentEmployeeByCustomer_CustIdAndEmployee_EmpIdAndDate(Integer custId,Integer empId,Date dates);
	
	@Query(value = "call M_GetAbsentDetailsByCustIdAndEmpId(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getEmployeeDetailsForAbsentEmployeeByCustomer_CustIdAndEmployee_EmpIdAndDate(Integer custId,Integer empId,Date dates);
	
	@Query(value = "call M_getShiftDetailsForLeaveByCustIdAndEmpId(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getShiftDetailsForAppLeaveByCustomer_CustIdAndEmployee_EmpIdAndDate(Integer custId,Integer empId,Date dates);
	
	@Query(value = "call M_getEmployeeLeaveDetailsByCustIdAndEmpId(?1,?2,?3)", nativeQuery = true)
	List<Object[]> getEmployeeDetailsForAppLeaveByCustomer_CustIdAndEmployee_EmpIdAndDate(Integer custId,Integer empId,Date dates);
	/*
	 * @Query(value =
	 * "UPDATE tmemployee SET empAppSetupStatus=:empAppSetupStatus WHERE empId=:empId"
	 * , nativeQuery = true)
	 * 
	 * @Transactional Employee update(@Param("empId") Integer
	 * empId, @Param("empAppSetupStatus") String empAppSetupStatus);
	 */
}
