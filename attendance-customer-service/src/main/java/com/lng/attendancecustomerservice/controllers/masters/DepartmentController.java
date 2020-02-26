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

import com.lng.attendancecustomerservice.service.masters.DepartmentService;
import com.lng.dto.masters.department.DepartmentDto;
import com.lng.dto.masters.department.DepartmentResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/department")
public class DepartmentController {
	@Autowired
	DepartmentService departmentService;

	@PostMapping(value = "/create")
	public ResponseEntity<DepartmentResponse> save(@RequestBody DepartmentDto departmentDto) {
		DepartmentResponse response = departmentService.saveDepartment(departmentDto);
		if (departmentDto !=null){
			return new ResponseEntity<DepartmentResponse>(response, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<DepartmentResponse> getAll() {
		DepartmentResponse departmentDto= departmentService.getAll();
		if(departmentDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<DepartmentResponse>(departmentDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateByDepartmentId")
	public ResponseEntity<status.Status> update(@RequestBody DepartmentDto departmentDto){
		status.Status status = departmentService.updateDepartmentByDepartmentId(departmentDto);
		if(departmentDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByDepartmentId")
	public ResponseEntity<DepartmentResponse> delete(@RequestBody DepartmentDto departmentDto) {
		DepartmentResponse departmentDto2 = departmentService.deleteByDeptId(departmentDto.getDeptId());
		if(departmentDto!=null){
			return new ResponseEntity<DepartmentResponse>(departmentDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}

	@PostMapping(value = "/getDepartmenDetailsByDepartmenId")
	public ResponseEntity<DepartmentResponse> findByShiftId(@RequestBody DepartmentDto departmentDto) {
		DepartmentResponse departmentResponse = departmentService.getDepartmentByDeptId(departmentDto.getDeptId());
		if (departmentResponse !=null){
			return new ResponseEntity<DepartmentResponse>(departmentResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getAllbyCustId")
	public ResponseEntity<DepartmentResponse> findByCustId(@RequestBody DepartmentDto departmentDto) {
		DepartmentResponse departmentResponse = departmentService.getAllByCustId(departmentDto.getRefCustId());
		return new ResponseEntity<DepartmentResponse>(departmentResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/getAllbyBrId")
	public ResponseEntity<DepartmentResponse> getByBrId(@RequestBody DepartmentDto departmentDto) {
		DepartmentResponse departmentResponse = departmentService.getAllByCustId(departmentDto.getBrId());
		return new ResponseEntity<DepartmentResponse>(departmentResponse, HttpStatus.OK);
	}
}

