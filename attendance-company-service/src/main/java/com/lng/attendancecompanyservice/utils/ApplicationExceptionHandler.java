package com.lng.attendancecompanyservice.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.lng.attendancecompanyservice.entity.exception.ApplicationException;
import com.lng.attendancecompanyservice.repositories.exception.ApplicationExceptionRepository;

public class ApplicationExceptionHandler {

	@Autowired
	ApplicationExceptionRepository applicationExceptionRepository;
	
	public void saveException(int custId, String moduleName, String exMessage, String stackTrace, String status, String remarks) {
		try {

			ApplicationException applicationException = new ApplicationException();
			applicationException.setRefCustId(custId);
			applicationException.setModuleName(moduleName);
			applicationException.setExMessage(exMessage);
			applicationException.setStackTrace(stackTrace);
			applicationException.setDateTime(new Date());
			applicationException.setStatus(status);
			applicationException.setRemarks(remarks);
			applicationExceptionRepository.save(applicationException);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
