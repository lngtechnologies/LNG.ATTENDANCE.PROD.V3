package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.StateService;
import com.lng.dto.state.StateDto;
import com.lng.dto.state.StateResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master")
public class StateController {
	@Autowired
	StateService stateService;

	@PostMapping(value = "/create")
	public ResponseEntity<StateResponse> save(@RequestBody StateDto stateDto) {
		StateResponse stateDto1 = stateService.saveState(stateDto);
		if (stateDto !=null){
			return new ResponseEntity<StateResponse>(stateDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<StateResponse> getAll() {
		StateResponse stateDto =  stateService.getAll();
		if(stateDto.getData().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<StateResponse>(stateDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateByStateId")
	public ResponseEntity<status.Status> update(@RequestBody StateDto stateDto){
		status.Status status = stateService.updateSateByStateId(stateDto);
		if(stateDto != null){
			return  new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByStateId")
	public ResponseEntity<StateResponse> delete(@RequestBody StateDto stateDto) {
		StateResponse stateDto1 = stateService.deleteByStateId(stateDto.getStateId());
		if(stateDto!=null){
			return new ResponseEntity<StateResponse>(stateDto1,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}
	
	@PostMapping(value = "/getStateDetailsByCountryId")
	public ResponseEntity<StateResponse> edit(@RequestBody StateDto StateDto) {
		StateResponse stateDto1 = stateService.getStateDetailsByRefCountryId(StateDto.getRefCountryId());
		if(stateDto1 !=null){
			return new ResponseEntity<StateResponse>(stateDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
