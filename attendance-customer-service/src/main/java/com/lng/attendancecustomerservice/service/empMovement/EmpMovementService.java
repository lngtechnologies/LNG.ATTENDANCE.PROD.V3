package com.lng.attendancecustomerservice.service.empMovement;

import java.util.Date;

import com.lng.dto.empMovement.EmpMovementDto;
import com.lng.dto.empMovement.EmpMovementParamResponse;
import com.lng.dto.empMovement.EmpMovementResponse;

public interface EmpMovementService {
	
	EmpMovementResponse  save(EmpMovementDto empMovementDto);
	
	EmpMovementResponse getAll(Integer refEmpId,Date empMovementDate);
	
	EmpMovementParamResponse getAllEmpVisitList(Integer empId);

}
