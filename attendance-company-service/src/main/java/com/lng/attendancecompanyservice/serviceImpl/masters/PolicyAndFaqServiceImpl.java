package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.PolicyAndFaq;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.repositories.masters.PolicyAndFaqRepository;
import com.lng.attendancecompanyservice.service.masters.PolicyAndFaqService;
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
	public PolicyAndFaqResponse save(PolicyAndFaqDto policyAndFaqDto) {
		PolicyAndFaqResponse  policyAndFaqResponse = new PolicyAndFaqResponse();
		try {
			PolicyAndFaq policyAndFaq = policyAndFaqRepository.findPolicyAndFaqByPageId(policyAndFaqDto.getPageId());
			if(policyAndFaq != null) {
				PolicyAndFaq policyAndFaq1  = policyAndFaqRepository.findPolicyAndFaqByValue(policyAndFaqDto.getValue());
				policyAndFaqRepository.updatePolicyAndFaqByValueAndPageId(policyAndFaqDto.getValue(),policyAndFaqDto.getPageId());
				policyAndFaqResponse.status = new Status(false,200, "successfully created");
			}else {
				policyAndFaqResponse.status = new Status(true,400, "Not Found");
			}
		}catch(Exception e) {
			policyAndFaqResponse.status = new Status(true,3000, e.getMessage()); 

		}

		return policyAndFaqResponse;
	}

	public PolicyAndFaqDto convertToPolicyAndFaqDto(PolicyAndFaq policyAndFaq) {
		PolicyAndFaqDto policyAndFaqDto = modelMapper.map(policyAndFaq,PolicyAndFaqDto.class);
		return policyAndFaqDto;
	}

	@Override
	public PolicyAndFaqResponse getPolicyAndFaqByKey(String key) {
		PolicyAndFaqResponse  policyAndFaqResponse = new PolicyAndFaqResponse();
		try {
			List<PolicyAndFaq> policyAndFaqList=policyAndFaqRepository.findAllByKey(key);
			policyAndFaqResponse.setData(policyAndFaqList.stream().map(policyAndFaq -> convertToPolicyAndFaqDto(policyAndFaq)).collect(Collectors.toList()));

			if(policyAndFaqResponse.getData().isEmpty()) {
				policyAndFaqResponse.status = new Status(true,400, "Not found"); 
			}else {
				policyAndFaqResponse.status = new Status(false,200, "success");
			}
		}catch(Exception e) {
			policyAndFaqResponse.status = new Status(true,3000, e.getMessage()); 

		}
		return policyAndFaqResponse;
	}

	@Override
	public Status update(PolicyAndFaqDto policyAndFaqDto) {
		//PolicyAndFaqResponse  policyAndFaqResponse = new PolicyAndFaqResponse();
		Status status = null;
		try {
			PolicyAndFaq policyAndFaq = policyAndFaqRepository.findPolicyAndFaqByPageId(policyAndFaqDto.getPageId());
			if(policyAndFaq != null) {
				PolicyAndFaq policyAndFaq1  = policyAndFaqRepository.findPolicyAndFaqByValue(policyAndFaqDto.getValue());
				policyAndFaqRepository.updatePolicyAndFaqByValueAndPageId(policyAndFaqDto.getValue(),policyAndFaqDto.getPageId());
				status = new Status(false, 200, "Updated successfully");
			}else {
				status = new Status(true, 400, " Not Found");
			}
		}catch(Exception e) {
			status = new Status(true, 500, e.getMessage());

		}

		return status;
	}
}
