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

import com.lng.attendancecompanyservice.service.masters.CompanyUserMgmtService;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleDto;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;
import com.lng.dto.masters.custUserMgmt.custUserParam;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/user")
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

	/*@PostMapping(value = "/findAllCompanyUsersByLoginId")
	public ResponseEntity<CompanyUserLoginModuleMapResponseDto> findAllUsersByLoginId(@RequestBody CompanyUserLoginModuleMapResponseDto companyUserLoginModuleMapResponseDto) {
		CompanyUserLoginModuleMapResponseDto status = companyUserMgmtService.getAllUserByLoginId(companyUserLoginModuleMapResponseDto.getLoginId());
		return new ResponseEntity<CompanyUserLoginModuleMapResponseDto>(status, HttpStatus.CREATED);	
	}*/
	@PostMapping(value = "/findAllUsersByCustId")
	public ResponseEntity<CompanyUserLoginModuleMapResponseDto> findAllUsersByCustId(@RequestBody CompanyUserLoginModuleMapResponseDto custUserBranchLoginMapDto) {
		CompanyUserLoginModuleMapResponseDto status = companyUserMgmtService.getAllUserByCustId(custUserBranchLoginMapDto.getCustId());
		return new ResponseEntity<CompanyUserLoginModuleMapResponseDto>(status, HttpStatus.CREATED);	
	}
	@GetMapping(value = "/findAllModules")
	public ResponseEntity<UserModuleResDto> findAllModules() {
		UserModuleResDto userModuleResponseDto = companyUserMgmtService.findAllModules();
		return new ResponseEntity<UserModuleResDto>(userModuleResponseDto, HttpStatus.CREATED);	
	}

	@PostMapping(value = "/update/userDetails/modules")
	public ResponseEntity<Status> updateAll(@RequestBody CompanyUserLoginModuleDto companyUserLoginModuleDto) {
		Status status = companyUserMgmtService.updateUserDetails(companyUserLoginModuleDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
