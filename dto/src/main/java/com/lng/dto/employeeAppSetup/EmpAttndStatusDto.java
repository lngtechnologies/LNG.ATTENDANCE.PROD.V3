package com.lng.dto.employeeAppSetup;

import java.util.Date;

import status.Status;

public class EmpAttndStatusDto {

	private String empAttndDate;
	
	private String empAttndInDateTime;
	
	private String empAttndOutDateTime;
	
	public Status status;

	public String getEmpAttndDate() {
		return empAttndDate;
	}

	public void setEmpAttndDate(String empAttndDate) {
		this.empAttndDate = empAttndDate;
	}

	public String getEmpAttndInDateTime() {
		return empAttndInDateTime;
	}

	public void setEmpAttndInDateTime(String empAttndInDateTime) {
		this.empAttndInDateTime = empAttndInDateTime;
	}

	public String getEmpAttndOutDateTime() {
		return empAttndOutDateTime;
	}

	public void setEmpAttndOutDateTime(String empAttndOutDateTime) {
		this.empAttndOutDateTime = empAttndOutDateTime;
	}
}
