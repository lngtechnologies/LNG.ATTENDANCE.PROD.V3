package com.lng.attendancecompanyservice.serviceImpl.exception;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.exception.ApplicationException;
import com.lng.attendancecompanyservice.repositories.exception.ApplicationExceptionRepository;
import com.lng.attendancecompanyservice.service.exception.ApplicationExceptionService;



@Service
public class ApplicationExceptionServiceImpl implements ApplicationExceptionService{

	@Autowired
	ApplicationExceptionRepository applicationExceptionRepository;
	
	public void saveException(int custId, String moduleName, String exMessage, String stackTrace, String status, String remarks) {
		
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
