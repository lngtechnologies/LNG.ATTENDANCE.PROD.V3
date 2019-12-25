package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.PolicyAndFaq;
import com.lng.attendancecustomerservice.repositories.empAppSetup.PolicyAndFaqRepository;
import com.lng.attendancecustomerservice.repositories.masters.BlockRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.PolicyAndFaqService;
import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

import status.Status;
@Service
public class PolicyAndFaqServiceImpl implements PolicyAndFaqService {
	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	BlockRepository blockRepository;
	@Autowired
	PolicyAndFaqRepository policyAndFaqRepository;
	
	@Override
	public PolicyAndFaqResponse getPolicyAndFaqByKey(String key) {
		PolicyAndFaqResponse  policyAndFaqResponse = new PolicyAndFaqResponse();
		try {
			List<PolicyAndFaq> policyAndFaqList=policyAndFaqRepository.findAllByKey(key);
			policyAndFaqResponse.setData(policyAndFaqList.stream().map(policyAndFaq -> convertToPolicyAndFaqDto(policyAndFaq)).collect(Collectors.toList()));

			if(policyAndFaqResponse.getData().isEmpty()) {
				policyAndFaqResponse.status = new Status(true,400, "Not found"); 
			}else {
				policyAndFaqResponse.status = new Status(false,200, "Success");
			}
		}catch(Exception e) {
			policyAndFaqResponse.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return policyAndFaqResponse;
	}
	
	public PolicyAndFaqDto convertToPolicyAndFaqDto(PolicyAndFaq policyAndFaq) {
		PolicyAndFaqDto policyAndFaqDto = modelMapper.map(policyAndFaq,PolicyAndFaqDto.class);
		return policyAndFaqDto;
	}

}
