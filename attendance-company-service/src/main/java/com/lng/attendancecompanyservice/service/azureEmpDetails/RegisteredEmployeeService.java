package com.lng.attendancecompanyservice.service.azureEmpDetails;

import com.lng.dto.empAzureDetails.ResponseDto;

public interface RegisteredEmployeeService {

	ResponseDto getRegisteredEmpDetailsByBranchId(Integer brId);
	
	void getPersistedFaceIds(String branchCode) throws Exception;;
}
