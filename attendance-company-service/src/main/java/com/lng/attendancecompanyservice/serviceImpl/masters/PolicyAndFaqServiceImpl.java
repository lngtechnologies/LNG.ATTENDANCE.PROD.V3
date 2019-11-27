package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Block;
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
			if(policyAndFaqDto.getFaqText() == null || policyAndFaqDto.getFaqText().isEmpty()) throw new Exception("Please enter Faq name");
			if(policyAndFaqDto.getPolicyText() == null || policyAndFaqDto.getPolicyText().isEmpty()) throw new Exception("Please enter Policy name");
			PolicyAndFaq policyAndFaq  =  new PolicyAndFaq();	
			policyAndFaq.setFaqText(policyAndFaqDto.getFaqText());
			policyAndFaq.setPolicyText(policyAndFaqDto.getPolicyText());
			policyAndFaqRepository.save(policyAndFaq);
			policyAndFaqResponse.status = new Status(false,200, "successfully created");

		}catch(Exception e) {
			policyAndFaqResponse.status = new Status(true,3000, e.getMessage()); 

		}

		return policyAndFaqResponse;
	}

	@Override
	public PolicyAndFaqResponse getAll() {
		PolicyAndFaqResponse  policyAndFaqResponse = new PolicyAndFaqResponse();
		try {
			List<PolicyAndFaq> policyAndFaqList=policyAndFaqRepository.findAll();
			policyAndFaqResponse.setData(policyAndFaqList.stream().map(policyAndFaq -> convertToPolicyAndFaqDto(policyAndFaq)).collect(Collectors.toList()));
			if(policyAndFaqResponse.getData().isEmpty()) {
				policyAndFaqResponse.status = new Status(false,4000, "Not found"); 
			}else {
				policyAndFaqResponse.status = new Status(false,200, "success");
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

	/*
	 * @Override public PolicyAndFaqResponse update(PolicyAndFaqDto policyAndFaqDto)
	 * {
	 * 
	 * PolicyAndFaqResponse policyAndFaqResponse = new PolicyAndFaqResponse(); try {
	 * //PolicyAndFaq policyAndFaq = policyAndFaqRepository.
	 * updatePolicyAndFaqByPolicyAndFaq_FaqTextAndPolicyAndFaq_PolicyText(
	 * policyAndFaqDto.getFaqText(), policyAndFaqDto.getPolicyText()); PolicyAndFaq
	 * policyAndFaq = new PolicyAndFaq();
	 * policyAndFaq.setFaqText(policyAndFaqDto.getFaqText());
	 * policyAndFaq.setPolicyText(policyAndFaqDto.getPolicyText());
	 * policyAndFaqRepository.save(policyAndFaq); policyAndFaqResponse.status = new
	 * Status(false,200, "Updated successfully");
	 * 
	 * }catch(Exception e) { policyAndFaqResponse.status = new Status(true,3000,
	 * e.getMessage());
	 * 
	 * }
	 * 
	 * return policyAndFaqResponse; }
	 */
}
