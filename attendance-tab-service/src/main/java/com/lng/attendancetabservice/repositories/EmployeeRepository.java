package com.lng.attendancetabservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Employee;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {
	
	@Query(value = "call generateOtp()", nativeQuery = true)
	int generateOtp();
	
	
	@Query(value = "SELECT emp.* FROM tmemployee emp WHERE refBrId =?1 AND refCustId =?2 AND empMobile=?3", nativeQuery = true)
	Employee checkEmployeeExistsOrNot(Integer refBrId,Integer refCustId,String empMobile);
	
	@Query(value = " SELECT empId,empName FROM tmemployee emp WHERE empMobile=?1", nativeQuery = true)
	List<Object[]> findEmployee(String empMobile);
	
	Employee findByempId(Integer empId);
	
}
