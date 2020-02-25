package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Employee;

@Repository
public interface CustEmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {

	List<Employee> findAllEmployeeByEmpMobile(String empMobile);
	
	List<Employee> findAllEmployeeByEmpMobileAndCustomer_CustId(String empMobile, Integer custId);
	
	Employee findEmployeeByEmpIdAndEmpInService(Integer empId, Boolean empInService);
	
	@Query(value = "SELECT * FROM tmemployee WHERE empId = (SELECT refEmpReportingToId FROM ttempreportingto WHERE refEmpId = ?1 AND empToDate IS NULL)",nativeQuery = true)
	Employee findEmpReportingToByEmpId(Integer empId);
	
	@Query(value = "CALL getEmpDetails();", nativeQuery = true)
	List<Object[]> findAllEmployeeByEmpInService();
	
	@Query(value = "CALL getEmpDetailsByEmpId(?1)", nativeQuery = true)
	List<Object[]> findAllEmployeeByEmpIdAndEmpInService(Integer empId);
	
	@Query(value = "CALL getEmpDetailsByCustId(?1)", nativeQuery = true)
	List<Object[]> findByCustomer_CustIdAndEmpInService(Integer custId);
	
	@Query(value = "CALL searchEmployeeByEmpName(?1)", nativeQuery = true)
	List<Employee> searchAllEmployeeByEmpName(String empName);
	
	@Query(value = "CALL subtractDaysFromDate(?1)", nativeQuery = true)
	Date subtractDaysFromDate(Date date);
	
	Employee findByEmpMobileAndCustomer_CustId(String empMobile, Integer custId);
	// Employee findEmployeeByReportionToId(Integer );
	
	@Query(value = "SELECT ed.refDeptId, d.deptName FROM ttempdept ed LEFT JOIN tmdepartment d ON d.deptId = ed.refDeptId WHERE ed.refEmpId = ?1", nativeQuery = true)
	List<Object[]> getReportingToDepartment(Integer reportingToId);
	
	List<Employee> findByBranch_BrId(Integer brId);
	
	@Query(value = "SELECT	* FROM tmemployee e LEFT JOIN ttempdept ed ON e.empId = ed.refEmpId LEFT JOIN tmdepartment d ON d.deptId = ed.refDeptId WHERE d.deptId = ?1 AND deptIsActive = TRUE", nativeQuery = true)
	List<Employee> findByDeptId(Integer deptId);
	
	@Query(value = "SELECT emp.empId,emp.empName FROM tmemployee emp LEFT JOIN ttlogindataright ldr ON ldr.refBrId = emp.refBrId WHERE  ldr.refLoginId =?1",nativeQuery = true)
	List<Object[]> findEmployeeByLoginDataRight_refLoginId(Integer refLoginId);
	
	@Query(value = "SELECT * FROM tmemployee WHERE refCustId = ?1 AND empInService = true AND empId NOT IN(SELECT refEmpId FROM ttlogin WHERE refCustId = ?1 and loginIsActive = true)", nativeQuery = true)
	List<Employee> findByCustomer_CustId(Integer custId);
	
	@Query(value = "CALL M_GetTodaysEmployeeSummary(?1,?2)", nativeQuery = true)
	List<Object[]> M_getEmployeeTodaysSummary(Integer custId, Integer empId);
	
	@Query(value = "CALL W_GetTodaysEmployeeSummary(?1,?2)", nativeQuery = true)
	List<Object[]> W_getEmployeeTodaysSummary(Integer custId, Integer loginId);
}
