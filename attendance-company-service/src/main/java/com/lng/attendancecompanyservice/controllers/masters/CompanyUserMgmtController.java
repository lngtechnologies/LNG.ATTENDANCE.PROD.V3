package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.CompanyUserMgmtService;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleDto;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.custUserParam;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/company/user")
public class CompanyUserMgmtController {

	@Autowired
	CompanyUserMgmtService companyUserMgmtService;

	@PostMapping(value = "/create")
	public ResponseEntity<Status> saveAllData(@RequestBody custUserParam userData) {
		CompanyUserLoginModuleDto companyUserLoginModuleDto = new CompanyUserLoginModuleDto();
		CustUserMgmtDto user = new CustUserMgmtDto();
		user.setUserName(userData.userDetails.getUserName());
		user.setuMobileNumber(userData.userDetails.getuMobileNumber());
		companyUserLoginModuleDto.setUserDetails(user);
		companyUserLoginModuleDto.setModules(userData.userDetails.modules);
		Status status = companyUserMgmtService.saveAllDetails(companyUserLoginModuleDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/delete")
	public ResponseEntity<Status> deleteByLoginId(@RequestBody CustUserLoginDto custUserLoginDto) {
		Status status = companyUserMgmtService.deleteByLoginId(custUserLoginDto.getLoginId());
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}


	@PostMapping(value = "/update/userDetails")
	public ResponseEntity<Status> updateUser(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		Status status = companyUserMgmtService.updateUserDetails(custUserMgmtDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/update/modules")
	public ResponseEntity<Status> updateModules(@RequestBody CustUserModuleMapDto custUserModuleMapDto) {
		Status status = companyUserMgmtService.updateModules(custUserModuleMapDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/findAllUsersByLoginId")
	public ResponseEntity<CompanyUserLoginModuleMapResponseDto> findAllUsersByLoginId(@RequestBody CompanyUserLoginModuleMapResponseDto companyUserLoginModuleMapResponseDto) {
		CompanyUserLoginModuleMapResponseDto status = companyUserMgmtService.getAllUserByLoginId(companyUserLoginModuleMapResponseDto.getLoginId());
		return new ResponseEntity<CompanyUserLoginModuleMapResponseDto>(status, HttpStatus.CREATED);	
	}


}
