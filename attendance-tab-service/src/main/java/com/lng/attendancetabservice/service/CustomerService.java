package com.lng.attendancetabservice.service;

import com.lng.dto.employeeAppSetup.OtpResponseDto;
import com.lng.dto.tabService.CustomerResponse1;

public interface CustomerService {
	
	OtpResponseDto generateOtp(String custCode, String brCode);
	
	CustomerResponse1  getCustBranchDetails(String custCode, String brCode);

}
