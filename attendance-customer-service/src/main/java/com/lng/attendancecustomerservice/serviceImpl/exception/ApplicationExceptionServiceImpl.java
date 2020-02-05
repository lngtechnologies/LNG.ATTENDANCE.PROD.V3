package com.lng.attendancecustomerservice.serviceImpl.exception;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.lng.attendancecustomerservice.entity.exception.ApplicationException;
import com.lng.attendancecustomerservice.repositories.exception.ApplicationExceptionRepository;
import com.lng.attendancecustomerservice.service.ApplicationExceptionService;

public class ApplicationExceptionServiceImpl implements ApplicationExceptionService {

	@Autowired
	ApplicationExceptionRepository applicationExceptionRepository;
	
	@Override
	public void saveEcxeption(int custId, String moduleName, String exMessage, String stackTrace, String status,
			String remarks) {
		try {
			ApplicationException applicationException = new ApplicationException();
			applicationException.setRefCustId(custId);
			applicationException.setModuleName(moduleName);
			applicationException.setExMessage(exMessage);
			applicationException.setStackTrace(stackTrace);
			applicationException.setStatus(status);
			applicationException.setRemarks(remarks);
			applicationException.setDateTime(new Date());
			applicationExceptionRepository.save(applicationException);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
