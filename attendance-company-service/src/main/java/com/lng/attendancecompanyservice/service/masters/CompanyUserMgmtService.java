package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleDto;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;

import status.Status;

public interface CompanyUserMgmtService {

	Status saveAllDetails(CompanyUserLoginModuleDto companyUserLoginModuleDto);

	Status deleteByLoginId(Integer loginId);

	Status updateUserDetails(CustUserMgmtDto custUserMgmtDto);

	Status updateModules(CustUserModuleMapDto custUserModuleMapDto);
	
	//CompanyUserLoginModuleMapResponseDto getAllUserByLoginId(Integer loginId);
	
	UserModuleResDto findAllModules();
	
	CompanyUserLoginModuleMapResponseDto getAllUserByCustId(Integer custId);
	
	Status updateUserDetails(CompanyUserLoginModuleDto companyUserLoginModuleDto);
	
	

}
