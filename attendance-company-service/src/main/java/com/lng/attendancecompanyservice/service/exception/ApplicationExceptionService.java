package com.lng.attendancecompanyservice.service.exception;


public interface ApplicationExceptionService {

	void saveException(int custId, String moduleName, String exMessage, String stackTrace, String status, String remarks);
}
