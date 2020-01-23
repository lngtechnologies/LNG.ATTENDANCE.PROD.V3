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

import com.lng.attendancecompanyservice.service.masters.BranchService;
import com.lng.dto.masters.branch.BranchDto;
import com.lng.dto.masters.branch.BranchResponse;
import com.lng.dto.masters.country.CountryResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/branch")
public class BranchController {
	@Autowired
	BranchService branchService;

	
	@PostMapping(value = "/create")
	public ResponseEntity<BranchResponse> save(@RequestBody BranchDto branchDto) {
		BranchResponse branchDto1 = branchService.saveBranch(branchDto);
		if (branchDto !=null){
			return new ResponseEntity<BranchResponse>(branchDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@GetMapping(value = "/getAll")
	public ResponseEntity<BranchResponse> getAll() {
		BranchResponse branchDto = branchService.getAll();
		if(branchDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<BranchResponse>(branchDto, HttpStatus.OK);
	}


	@PostMapping(value = "/deleteByBranchId")
	public ResponseEntity<BranchResponse> delete(@RequestBody BranchDto branchDto) {
		BranchResponse branchDto2 = branchService.deleteByBrId(branchDto.getBrId());
		if(branchDto!=null){
			return new ResponseEntity<BranchResponse>(branchDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}


	@PostMapping(value="/updateByBranchId")
	public ResponseEntity<status.Status> update(@RequestBody BranchDto branchDto){
		status.Status status = branchService.updateBranchByBrId(branchDto);
		if(branchDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/getBranchDetailsByBranchId")
	public ResponseEntity<BranchResponse> findByBranchId(@RequestBody BranchDto branchDto) {
		BranchResponse branchResponse = branchService.getBranchByBrId(branchDto.getBrId());
		if (branchResponse !=null){
			return new ResponseEntity<BranchResponse>(branchResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<BranchResponse> getAllByCustId(@RequestBody BranchDto branchDto) {
		BranchResponse branchDto2 = branchService.getAllByCustId(branchDto.getRefCustomerId());
		if(branchDto!=null){
			return new ResponseEntity<BranchResponse>(branchDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}
	/*@PostMapping(value = "/getBranchByCustId")
	public ResponseEntity<BranchResponse> findBranchByCustId(@RequestBody BranchDto branchDto) {
		BranchResponse branchResponse = branchService.findBranchList(branchDto.getRefCustomerId());
		if (branchResponse !=null){
			return new ResponseEntity<BranchResponse>(branchResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}*/

}