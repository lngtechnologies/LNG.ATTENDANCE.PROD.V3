package com.lng.attendancecustomerservice.repositories.notification;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.notification.EmpToken;

@Repository
public interface EmpTokenRepository extends PagingAndSortingRepository<EmpToken, Integer> {

	EmpToken findByEmployee_EmpId(Integer empId);
	
	@Query(value = "SELECT et.* FROM ttemptoken et LEFT JOIN tmemployee emp ON emp.empId = et.refEmpId LEFT JOIN tmbranch br ON br.brId = emp.refBrId WHERE br.brId = ?1 AND emp.empInService = TRUE", nativeQuery = true)
	List<EmpToken> findByBranchId(Integer brId);
	
	@Query(value = "SELECT et.* FROM ttemptoken et LEFT JOIN tmemployee emp ON emp.empId = et.refEmpId LEFT JOIN ttempdept ed ON ed.refEmpId = emp.empId LEFT JOIN tmdepartment d ON d.deptId = ed.refDeptId WHERE d.deptId = ?1 AND emp.empInService = TRUE AND d.deptIsActive = TRUE", nativeQuery = true)
	List<EmpToken> findByDeptId(Integer deptId);
	
	EmpToken findByEmployee_EmpIdAndIsActive(Integer empId,Boolean isActive);
}
