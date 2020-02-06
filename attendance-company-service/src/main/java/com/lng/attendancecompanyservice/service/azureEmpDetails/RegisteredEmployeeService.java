package com.lng.attendancecompanyservice.service.azureEmpDetails;

import java.util.List;

import org.apache.http.HttpEntity;

import com.lng.dto.empAzureDetails.AzureLargeFaceListResponseDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsResponseDto;
import com.lng.dto.empAzureDetails.ResponseDto;

import status.Status;

public interface RegisteredEmployeeService {

	AzurePersistedFaceIdsResponseDto getPersistedFaceIdByBranchId(Integer brId);
	
	AzureLargeFaceListResponseDto getAllFaceList();
	//List<AzureFacelistDto> getPersistedFaceIds(String branchCode) throws Exception;;
	
	Status deleteLargeFacelist(String largeFacelist);
	
	Status deletePersistedFaceId(String faceId);
}
