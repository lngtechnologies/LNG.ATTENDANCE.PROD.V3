package com.lng.attendancecustomerservice.service;

public interface ApplicationExceptionService {

	void saveEcxeption(int custId, String moduleName, String exMessage, String stackTrace, String status, String remarks);
}
