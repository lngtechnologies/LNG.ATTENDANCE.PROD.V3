package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.custUserMgmt.CustEmployeeResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDataRightResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchLoginMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserModulesDto;
import com.lng.dto.masters.custUserMgmt.CustUserResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserRightResponseDto;
import com.lng.dto.masters.custUserMgmt.UserModuleDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResponseDto;

import status.Status;

public interface CustUserMgmtService {

	CustUserResponseDto save(CustUserMgmtDto custUserMgmtDto);
	
	CustUserRightResponseDto getAssignedAndUnAssignedUserRights(Integer loginId, Integer custId);
	
	CustLoginDataRightResponseDto getAssignedAndUnAssignedLoginDataRights(Integer loginId, Integer custId);
	
	Status addModules(CustUserModuleMapDto custUserModuleMapDto);
	
	Status addBranchLoginDataRight(CustUserBranchLoginMapDto custUserBranchLoginMapDto);
	
	Status updateUserDetails(CustUserMgmtDto custUserMgmtDto);
	
	Status updateModules(CustUserModuleMapDto custUserModuleMapDto);
	
	Status updateBranchLoginDataRight(CustUserBranchLoginMapDto custUserBranchLoginMapDto);
	
	CustUserLoginModuleBranchMapResponseDto getAllByCustId(Integer custId);
	
	CustUserLoginModuleBranchMapResponseDto findAll();
	
	Status deleteByLoginId(Integer loginId);
	
	Status resetPasswordByLoginId(Integer loginId);
	
	CustLoginDto getLoginDetailsByLoginId(Integer loginId);
	
	CustEmployeeResponseDto getEmployeeByCustId(Integer custId);
	
	Status saveAllDetails(CustUserLoginModuleBranchDto custUserLoginModuleBranchDto);
	
	Status checkUserName(CustUserMgmtDto custUserMgmtDto);
	
	UserModuleResDto findAllModules();
}
