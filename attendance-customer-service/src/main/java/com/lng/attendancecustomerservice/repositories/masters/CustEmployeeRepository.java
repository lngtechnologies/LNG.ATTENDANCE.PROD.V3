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
	
	@Query(value = "SELECT * FROM tmemployee WHERE empId = (SELECT empReportingTo FROM tmemployee WHERE empId = ?1)",nativeQuery = true)
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
}
