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

import com.lng.attendancecustomerservice.service.masters.CustUserMgmtService;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custUserMgmt.CustEmployeeResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDataRightResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchLoginMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchResDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserRightResponseDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/cust/user")
public class CustUserMgmtController {

	@Autowired
	CustUserMgmtService custUserMgmtService;

	@PostMapping(value = "/create")
	public ResponseEntity<CustUserResponseDto> save(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		CustUserResponseDto custUserResponseDto = custUserMgmtService.save(custUserMgmtDto);
		if (custUserResponseDto !=null){
			return new ResponseEntity<CustUserResponseDto>(custUserResponseDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/getAssignedAndUnAssignedModules")
	public ResponseEntity<CustUserRightResponseDto> assignedAndUnAssignedModules(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		CustUserRightResponseDto custUserRightResponseDto = custUserMgmtService.getAssignedAndUnAssignedUserRights(custUserMgmtDto.getLoginId(), custUserMgmtDto.getCustomerId());
		if (custUserRightResponseDto !=null){
			return new ResponseEntity<CustUserRightResponseDto>(custUserRightResponseDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/getAssignedAndUnAssignedLoginDataRights")
	public ResponseEntity<CustLoginDataRightResponseDto> assignedAndUnAssignedDataRights(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		CustLoginDataRightResponseDto custLoginDataRightResponseDto = custUserMgmtService.getAssignedAndUnAssignedLoginDataRights(custUserMgmtDto.getLoginId(), custUserMgmtDto.getCustomerId());
		if (custLoginDataRightResponseDto !=null){
			return new ResponseEntity<CustLoginDataRightResponseDto>(custLoginDataRightResponseDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/add/modules")
	public ResponseEntity<Status> addModules(@RequestBody CustUserModuleMapDto custUserModuleMapDto) {
		Status status = custUserMgmtService.addModules(custUserModuleMapDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/add/branches")
	public ResponseEntity<Status> save(@RequestBody CustUserBranchLoginMapDto custUserBranchLoginMapDto) {
		Status status = custUserMgmtService.addBranchLoginDataRight(custUserBranchLoginMapDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/update/userDetails")
	public ResponseEntity<Status> updateUser(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		Status status = custUserMgmtService.updateUserDetails(custUserMgmtDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/update/modules")
	public ResponseEntity<Status> updateModules(@RequestBody CustUserModuleMapDto custUserModuleMapDto) {
		Status status = custUserMgmtService.updateModules(custUserModuleMapDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/update/branches")
	public ResponseEntity<Status> updateBranches(@RequestBody CustUserBranchLoginMapDto custUserBranchLoginMapDto) {
		Status status = custUserMgmtService.updateBranchLoginDataRight(custUserBranchLoginMapDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/findByCustId")
	public ResponseEntity<CustUserLoginModuleBranchMapResponseDto> findAllByCustId(@RequestBody CustUserLoginModuleBranchMapResponseDto custUserBranchLoginMapDto) {
		CustUserLoginModuleBranchMapResponseDto status = custUserMgmtService.getAllByCustId(custUserBranchLoginMapDto.getCustId());
		return new ResponseEntity<CustUserLoginModuleBranchMapResponseDto>(status, HttpStatus.CREATED);	
	}

	@GetMapping(value = "/findAll")
	public ResponseEntity<CustUserLoginModuleBranchMapResponseDto> findAll() {
		CustUserLoginModuleBranchMapResponseDto status = custUserMgmtService.findAll();
		return new ResponseEntity<CustUserLoginModuleBranchMapResponseDto>(status, HttpStatus.CREATED);	
	}

	@PostMapping(value = "/delete")
	public ResponseEntity<Status> deleteByLoginId(@RequestBody CustUserLoginDto custUserLoginDto) {
		Status status = custUserMgmtService.deleteByLoginId(custUserLoginDto.getLoginId());
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/reset/password")
	public ResponseEntity<Status> resetPassword(@RequestBody CustUserLoginDto custUserLoginDto) {
		Status status = custUserMgmtService.resetPasswordByLoginId(custUserLoginDto.getLoginId());
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/findByLoginId")
	public ResponseEntity<CustLoginDto> findAllByCustId(@RequestBody CustLoginDto custLoginDto) {
		CustLoginDto custLoginDto1 = custUserMgmtService.getLoginDetailsByLoginId(custLoginDto.getLoginId());
		return new ResponseEntity<CustLoginDto>(custLoginDto1, HttpStatus.CREATED);	
	}
	
	@PostMapping(value = "/findAllEmployeesByCustomerId")
    public ResponseEntity<CustEmployeeResponseDto> findByCustomerId(@RequestBody CustEmployeeDtoTwo custEmployeeDtoTwo) {
		CustEmployeeResponseDto custEmployeeListResponse = custUserMgmtService.getEmployeeByCustId(custEmployeeDtoTwo.getCustId());
        if (custEmployeeListResponse !=null){
            return new ResponseEntity<CustEmployeeResponseDto>(custEmployeeListResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	

	@PostMapping(value = "/findAllUsersByCustId")
	public ResponseEntity<CustUserLoginModuleBranchMapResponseDto> findAllUsersByCustId(@RequestBody CustUserLoginModuleBranchMapResponseDto custUserBranchLoginMapDto) {
		CustUserLoginModuleBranchMapResponseDto status = custUserMgmtService.getAllUserByCustId(custUserBranchLoginMapDto.getCustId());
		return new ResponseEntity<CustUserLoginModuleBranchMapResponseDto>(status, HttpStatus.CREATED);	
	}

	@PostMapping(value = "/save/all/data")
	public ResponseEntity<Status> saveAllData(@RequestBody CustUserLoginModuleBranchDto custUserLoginModuleBranchDto) {
		Status status = custUserMgmtService.saveAllDetails(custUserLoginModuleBranchDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/check/userName")
	public ResponseEntity<Status> checkUserName(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		Status status = custUserMgmtService.checkUserName(custUserMgmtDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/findAllModules")
	public ResponseEntity<UserModuleResDto> findAllModules() {
		UserModuleResDto userModuleResponseDto = custUserMgmtService.findAllModules();
		return new ResponseEntity<UserModuleResDto>(userModuleResponseDto, HttpStatus.CREATED);	
	}
	
	@PostMapping(value = "/findAllBranchesByCustId")
	public ResponseEntity<CustUserBranchResDto> findAllBranchesByCustId(@RequestBody CustLoginDto custLoginDto) {
		CustUserBranchResDto custLoginDto1 = custUserMgmtService.findAllBranchesByCustId(custLoginDto.getCustId());
		return new ResponseEntity<CustUserBranchResDto>(custLoginDto1, HttpStatus.CREATED);	
	}
}
