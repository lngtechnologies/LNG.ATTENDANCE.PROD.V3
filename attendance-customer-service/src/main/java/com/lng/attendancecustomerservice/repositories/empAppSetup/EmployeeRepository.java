package com.lng.attendancecustomerservice.repositories.empAppSetup;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.empAppSetup.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call getEmpDetailsByCustCodeAndEmpMobile(?1, ?2, ?3)", nativeQuery = true)
	Employee getEmployeeDetailsByCustomer_CustCodeAndEmployee_EmpMobile(String custCode, String empMobile, Integer custId);
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();
	
	@Query(value = "select * from tmemployee where refCustId = ?1 and empId = ?2", nativeQuery = true)
	Employee getByEmpIdAndRefCustId(Integer custId, Integer empId);
	
	Employee getEmployeeByEmpId(Integer empId);
	
	/*
	 * @Query(value =
	 * "UPDATE tmemployee SET empAppSetupStatus=:empAppSetupStatus WHERE empId=:empId"
	 * , nativeQuery = true)
	 * 
	 * @Transactional Employee update(@Param("empId") Integer
	 * empId, @Param("empAppSetupStatus") String empAppSetupStatus);
	 */
}
