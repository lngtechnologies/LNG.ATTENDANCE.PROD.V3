package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.ShiftService;
import com.lng.dto.masters.shift.ShiftDto;
import com.lng.dto.masters.shift.ShiftResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/shift")
public class ShiftController {

	@Autowired
	ShiftService shiftService;

	@PostMapping(value = "/create")
	public ResponseEntity<ShiftResponse> save(@RequestBody ShiftDto shiftDto) {
		ShiftResponse shiftDto1 = shiftService.saveShift(shiftDto);
		if (shiftDto !=null){
			return new ResponseEntity<ShiftResponse>(shiftDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<ShiftResponse> getAll() {
		ShiftResponse shiftDto = shiftService.getAll();
		if(shiftDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<ShiftResponse>(shiftDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateByShiftId")
	public ResponseEntity<status.Status> update(@RequestBody ShiftDto shiftDto){
		status.Status status = shiftService.updateShiftByShiftId(shiftDto);
		if(shiftDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByShiftId")
	public ResponseEntity<ShiftResponse> delete(@RequestBody ShiftDto shiftDto) {
		ShiftResponse shiftResponse = shiftService.deleteByShiftId(shiftDto.getShiftId());
		if(shiftDto!=null){
			return new ResponseEntity<ShiftResponse>(shiftResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}

	@PostMapping(value = "/getShiftDetailsByBranchId")
	public ResponseEntity<ShiftResponse> edit(@RequestBody ShiftDto shiftDto) {
		ShiftResponse shiftDto1 = shiftService.getShiftDetailsByRefBrId(shiftDto.getRefBrId());
		if(shiftDto !=null){
			return new ResponseEntity<ShiftResponse>(shiftDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/getShiftDetailsByShiftId")
	public ResponseEntity<ShiftResponse> findByShiftId(@RequestBody ShiftDto shiftDto) {
		ShiftResponse shiftResponse = shiftService.getShiftDetailsByShiftId(shiftDto.getShiftId());
		if (shiftResponse !=null){
			return new ResponseEntity<ShiftResponse>(shiftResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<ShiftResponse> getAllByCustId(@RequestBody ShiftDto shiftDto) {
		ShiftResponse shiftResponse = shiftService.getAllByCustId(shiftDto.getRefCustId());
		return new ResponseEntity<ShiftResponse>(shiftResponse, HttpStatus.OK);
	}
}