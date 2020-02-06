package com.lng.attendancecompanyservice.controllers.azureFaceList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.azureEmpDetails.RegisteredEmployeeService;
import com.lng.dto.customer.BranchDto;
import com.lng.dto.empAzureDetails.AzureLargeFaceListDto;
import com.lng.dto.empAzureDetails.AzureLargeFaceListResponseDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsResponseDto;
import com.lng.dto.masters.beacon.BeaconListResponseDto;
import com.lng.dto.masters.block.BlockDto;

import status.Status;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/azure/fecelist")
public class AzureFacelistController {

	@Autowired
	RegisteredEmployeeService registeredEmployeeService;
	
	@PostMapping(value = "/getAllPersistedFaceIdsByBranchId")
	public ResponseEntity<AzurePersistedFaceIdsResponseDto> edit1(@RequestBody BranchDto branchDto){
		AzurePersistedFaceIdsResponseDto status = registeredEmployeeService.getPersistedFaceIdByBranchId(branchDto.getBrId());
		return new ResponseEntity<AzurePersistedFaceIdsResponseDto>(status, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getAllLargeFaceList")
	public ResponseEntity<AzureLargeFaceListResponseDto> findAll() {
		AzureLargeFaceListResponseDto azureLargeFaceListResponseDto = registeredEmployeeService.getAllFaceList();
		if(azureLargeFaceListResponseDto.getLargeFaceList().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<AzureLargeFaceListResponseDto>(azureLargeFaceListResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value="/delete/largeFacelist")
	public ResponseEntity<Status> deleteFacelist(@RequestBody AzureLargeFaceListDto azureLargeFaceListDto){
		Status status = registeredEmployeeService.deleteLargeFacelist(azureLargeFaceListDto.getLargeFaceListId());
		if(status != null){
			return  new ResponseEntity<Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<Status>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value="/delete/persistedFaceId")
	public ResponseEntity<Status> deletePersistedFaceId(@RequestBody AzurePersistedFaceIdsDto azurePersistedFaceIdsDto){
		Status status = registeredEmployeeService.deletePersistedFaceId(azurePersistedFaceIdsDto.getPersistedFaceId());
		if(status != null){
			return  new ResponseEntity<Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<Status>(HttpStatus.NO_CONTENT);
	}
}
