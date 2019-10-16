package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.employeeType.EmployeeTypeDto;
import com.lng.dto.employeeType.EmployeeTypeListResponseDto;
import com.lng.dto.employeeType.StatusDto;

public interface EmployeeTypeService {

	StatusDto save(EmployeeTypeDto employeeTypeDto);
	
	EmployeeTypeListResponseDto findAll();
	
	StatusDto updateEmpType(EmployeeTypeDto employeeTypeDto);
	
	StatusDto deleteEmpTypeByEmpTypeId(Integer empTypeId);
}
