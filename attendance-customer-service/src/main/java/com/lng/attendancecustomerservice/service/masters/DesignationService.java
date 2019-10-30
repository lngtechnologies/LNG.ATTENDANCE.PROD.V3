package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.designation.DesignationDto;
import com.lng.dto.masters.designation.DesignationResponse;

import status.Status;

public interface DesignationService {

	DesignationResponse saveDesignation(DesignationDto designationDto);

	DesignationResponse getAll();
	Status updateDesignationBydesignationId(DesignationDto designationDto); 
	DesignationResponse deleteByDesignationId(Integer designationId);
	DesignationResponse getDesignationByDesignationId(Integer designationId);


}
