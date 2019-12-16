package com.lng.attendancetabservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Customer;
import com.lng.attendancetabservice.entity.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();
	
	
	@Query(value = "SELECT emp.* FROM tmemployee emp WHERE refBrId =?1 AND refCustId =?2 AND empName=?3 AND empMobile=?4", nativeQuery = true)
	Employee checkEmployeeExistsOrNot(Integer refBrId,Integer refCustId,String empName,String empMobile);
	
	@Query(value = " SELECT empId FROM tmemployee emp WHERE empName=?1 AND empMobile=?2", nativeQuery = true)
	List<Object[]> findEmployee(String empName,String empMobile);
}
