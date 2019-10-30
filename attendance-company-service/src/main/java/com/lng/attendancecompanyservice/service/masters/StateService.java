package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.state.StateDto;
import com.lng.dto.masters.state.StateResponse;

import status.Status;

public interface StateService {

	StateResponse saveState(StateDto stateDto);

	StateResponse getAll();
	Status updateSateByStateId(StateDto stateDto); 
	StateResponse deleteByStateId(Integer stateId);
	StateResponse getStateDetailsByRefCountryId(Integer refCountryId);
	StateResponse  getStateDetailsByStateId(Integer stateId);

}
