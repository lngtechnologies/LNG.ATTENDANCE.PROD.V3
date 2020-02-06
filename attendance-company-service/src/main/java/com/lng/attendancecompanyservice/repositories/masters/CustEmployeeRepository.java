package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.Employee;

@Repository
public interface CustEmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {

	List<Employee> findAllEmployeeByEmpMobile(String empMobile);
	
	Employee findEmployeeByEmpIdAndEmpInService(Integer empId, Boolean empInService);
	
	@Query(value = "SELECT * FROM tmemployee WHERE empId = (SELECT empReportingTo FROM tmemployee WHERE empId = ?1)",nativeQuery = true)
	Employee findEmpReportingToByEmpId(Integer empId);
	
	List<Employee> findAllEmployeeByEmpInService(Boolean empInService);
	
	@Query(value = "CALL searchEmployeeByEmpName(?1)", nativeQuery = true)
	List<Employee> searchAllEmployeeByEmpName(String empName);
	
	List<Employee> findByBranch_BrId(Integer brId);
	
	Employee getEmployeeByEmpPresistedFaceId(String faceId);
	
	// Employee findEmployeeByReportionToId(Integer );
}
