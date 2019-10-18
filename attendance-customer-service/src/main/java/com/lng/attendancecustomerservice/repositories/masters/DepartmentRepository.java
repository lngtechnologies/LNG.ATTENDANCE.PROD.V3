package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Department;
@Repository
public interface DepartmentRepository extends CrudRepository<Department,Integer> {	

	List<Department> findAll();
	@Query(value = "select * from tmdepartment where deptName = ?1", nativeQuery = true)
	Department findByDepartmentName(String stateName);
	Department findDepartmentByDeptId(Integer deptId);

	@Query(value = "CALL DepartmentIdIsExistOrNot(?1)",nativeQuery = true)
	int  findEmployeeDepartmentByDepartmentDeptId(int deptId);
}
