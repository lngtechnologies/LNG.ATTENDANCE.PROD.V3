package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.department.DepartmentDto;
import com.lng.dto.masters.department.DepartmentParamResponse;
import com.lng.dto.masters.department.DepartmentResponse;

import status.Status;

public interface DepartmentService {
	DepartmentResponse saveDepartment(DepartmentDto departmentDto);
	DepartmentResponse getAll();
	Status updateDepartmentByDepartmentId(DepartmentDto departmentDto);
	DepartmentResponse deleteByDeptId(Integer deptId);
	DepartmentResponse getDepartmentByDeptId(Integer deptId);
	DepartmentResponse getAllByCustId(Integer custId);
	DepartmentResponse getDepartmentDetailsByBrId(Integer brId);
	
	DepartmentParamResponse getDepartmentdetailByCustIdAndEmpId(Integer custId,Integer empId);
	

}
