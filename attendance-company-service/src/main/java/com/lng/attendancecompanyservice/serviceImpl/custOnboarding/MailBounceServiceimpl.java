package com.lng.attendancecompanyservice.serviceImpl.custOnboarding;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.custOnboarding.MailBounce;
import com.lng.attendancecompanyservice.entity.masters.IndustryType;
import com.lng.attendancecompanyservice.repositories.custOnboarding.MailBounceRepository;
import com.lng.attendancecompanyservice.service.custOnboarding.MailBounceService;
import com.lng.dto.customer.MailBounceDto;
import com.lng.dto.masters.industryType.IndustryTypeDto;

@Service
public class MailBounceServiceimpl implements MailBounceService {
	
	@Autowired
	MailBounceRepository mailBounceRepository;
	
	ModelMapper modelMapper = new ModelMapper();;

	@Override
	public MailBounceDto save(MailBounceDto mailBounceDto) {
		// MailBounce mailBounce = new MailBounce();
		try {
			MailBounce mailBounce = modelMapper.map(mailBounceDto, MailBounce.class);
			mailBounce.setResponse(mailBounceDto.getEmail() +" "+ mailBounceDto.getEvent());
			mailBounceRepository.save(mailBounce);
		} catch (Exception e) {
			
		}
		return mailBounceDto;
	}

	public MailBounceDto convertToMailBounceDto(MailBounce mailBounce) {
		MailBounceDto  mailBounceDto = modelMapper.map(mailBounce, MailBounceDto.class);
		return mailBounceDto;
	}
}
