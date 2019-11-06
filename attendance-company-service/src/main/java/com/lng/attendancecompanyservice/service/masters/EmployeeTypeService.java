package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.employeeType.EmployeeTypeDto;
import com.lng.dto.masters.employeeType.EmployeeTypeListResponseDto;

public interface EmployeeTypeService {

	EmployeeTypeListResponseDto findAll();
	
	EmployeeTypeDto findById(Integer empTypeId);
}
