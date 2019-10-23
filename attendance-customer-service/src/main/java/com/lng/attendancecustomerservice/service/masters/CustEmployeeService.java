package com.lng.attendancecustomerservice.service.masters;


import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import status.StatusDto;

public interface CustEmployeeService {

	StatusDto save(CustEmployeeDto custEmployeeDto);
}
