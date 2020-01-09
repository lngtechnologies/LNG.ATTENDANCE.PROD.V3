package com.lng.attendancetabservice.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();

	@Query(value = "call getOutPermisableTimeByEmpIdAndCustId(?1,?2)", nativeQuery = true)
	String getOutPermissibleTimeByEmployee_EmpIdAndCustomer_CustId(Integer empId,Integer custId);
	
	
	@Query(value = "SELECT e.* FROM tmemployee e WHERE e.refBrId =?1 AND e.refCustId =?2 AND empMobile=?3 AND empInService=TRUE", nativeQuery = true)
	Employee checkEmployeeExistsOrNot(Integer refBrId,Integer refCustId,String empMobile);
	
	//@Query(value = " SELECT empId,empName FROM tmemployee emp WHERE empMobile=?1", nativeQuery = true)
	//List<Object[]> findEmployee(String empMobile);
	
	Employee findByempId(Integer empId);
	
	@Query(value = "SELECT e.* FROM tmemployee e WHERE e.empMobile = ?1 AND e.refCustId = ?2 AND e.empInService=TRUE", nativeQuery = true)
	Employee findEmployeeByEmpMobileAndCustomer_custId(String empMobile,Integer refCustId);
	
	@Query(value = "SELECT e.* FROM tmemployee e WHERE e.empMobile = ?1 AND e.empInService=TRUE", nativeQuery = true)
	Employee findEmployee(String empMobile);
}
