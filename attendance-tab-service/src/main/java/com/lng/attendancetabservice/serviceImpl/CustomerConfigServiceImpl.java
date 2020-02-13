package com.lng.attendancetabservice.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.repositories.CustomerConfigRepository;
import com.lng.attendancetabservice.service.CustomerConfigService;
import com.lng.dto.masters.customerConfig.CustomerConfigDto;
import com.lng.dto.masters.customerConfig.CustomerConfigParamDto;
import com.lng.dto.masters.customerConfig.CustomerConfigResponse;

import status.Status;
@Service
public class CustomerConfigServiceImpl implements CustomerConfigService {

	@Autowired
	CustomerConfigRepository customerConfigRepository;
	@Override
	public CustomerConfigResponse getConfigDetails(Integer custId, Integer brId) {
		CustomerConfigResponse customerConfigResponse = new CustomerConfigResponse();
		List<CustomerConfigParamDto> customerConfigDtoList = new ArrayList<>();
		try {
			List<Object[]> customerConfigList = customerConfigRepository.findByCustomer_CustIdAndBranch_BrId(custId, brId);
			if(customerConfigList.isEmpty()) {
				customerConfigResponse.status = new Status(true, 400, "Configurations not found");
			} else {
				for (Object[] p : customerConfigList) {	
					CustomerConfigParamDto customerConfigParamDto = new CustomerConfigParamDto();
					customerConfigParamDto.setConfig((p[0].toString()));
					customerConfigParamDto.setStatusFlag((Boolean)p[1]);
					customerConfigDtoList.add(customerConfigParamDto);
					customerConfigResponse.setCustId(custId);
					customerConfigResponse.setBrId(brId);
					customerConfigResponse.setConfigList(customerConfigDtoList);
					customerConfigResponse.status = new Status(false,200, "Success");
				}
			}

		}catch (Exception e){
			customerConfigResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return customerConfigResponse;
	}

}
