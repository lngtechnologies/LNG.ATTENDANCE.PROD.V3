package com.lng.attendancecompanyservice.service.azureEmpDetails;

import com.lng.dto.empAzureDetails.AzureFacelistResponseDto;
import com.lng.dto.empAzureDetails.ResponseDto;

import status.Status;

public interface RegisteredEmployeeService {

	AzureFacelistResponseDto getRegisteredEmpDetailsByBranchId(Integer brId);
	
	void getPersistedFaceIds(String branchCode) throws Exception;;
}
