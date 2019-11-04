package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Department;
@Repository
public interface DepartmentRepository extends CrudRepository<Department,Integer> {	

	List<Department> findAllByDeptIsActive(Boolean deptIsActive);
	@Query(value = "select * from tmdepartment where deptName = ?1", nativeQuery = true)
	Department findByDepartmentName(String stateName);
	
	Department findDepartmentByDeptId(Integer deptId);

	@Query(value = "CALL DepartmentIdIsExistOrNot(?1)",nativeQuery = true)
	int  findEmployeeDepartmentByDepartmentDeptId(int deptId);
	
	@Query(value = "CALL CheckCustomerExistForDepartment(?1, ?2)",nativeQuery = true)
	int  findByRefCustIdAndDeptName(Integer refCustId,String deptName);
	
	
	Department findDepartmentBydeptNameAndCustomer_custId(String deptName, int custId);
	
	@Query(value = "SELECT a.* FROM tmdepartment a LEFT JOIN ttempdept b ON a.deptId = b.refDeptId WHERE b.refEmpId = ?1",nativeQuery = true)
	Department findDepartmentByEmployee_EmpId(Integer empId);
	
	List<Department> findAllByCustomer_CustIdAndDeptIsActive(int custId, Boolean deptIsActive);
}
