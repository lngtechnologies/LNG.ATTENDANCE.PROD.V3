package com.lng.attendancetabservice.service;

import com.lng.dto.masters.customerConfig.CustomerConfigResponse;

public interface CustomerConfigService {
	
	CustomerConfigResponse getConfigDetails(Integer custId,Integer brId);

}
