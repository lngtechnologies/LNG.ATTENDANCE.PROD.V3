package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeDepartment;
@Repository
public interface EmployeeDepartmentRepository extends CrudRepository<EmployeeDepartment,Integer> {

	EmployeeDepartment findByEmployee_EmpId(Integer empId);
	
	@Query(value = "select max(edpt.empFromDate) as empFromDate, edpt.empDeptId, edpt.refEmpId, edpt.refDeptId, edpt.empToDate from ttempdept edpt where edpt.refEmpId = ?1 and edpt.empToDate is null", nativeQuery = true)
	EmployeeDepartment findByEmpId(Integer empId);
	
	EmployeeDepartment findByEmployee_EmpIdAndDepartment_DeptIdAndEmpFromDate(Integer empId, Integer deptId, Date empFromDate);
}
