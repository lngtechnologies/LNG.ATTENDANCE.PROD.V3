package com.lng.dto.employeeAppSetup;

import java.util.Date;

public class EmployeeDataDto {

	private Integer custId;

	private String custName;
	
	private String custAddress;
	
	private Integer brId;
	
	private String brName;

	private String brCode;

	private Integer empId;

	private String employeeName;

	private Boolean appStatus;
	
	private String empPresistedFaceId;
	
	private Boolean empAttndStatus;
	
	private String empAttndancedate;

	private String empAttndanceInDateTime;
	
	private String empAttndanceOutDateTime;
	
	private String custLogo;
	
	

	//private Boolean empAppSetupStatus;

	public EmployeeDataDto(Integer custId, String custName, Integer brId, String brName, String brCode, Integer empid, String empName, 
						   String custLogo, Boolean appStatus, String empPresistedFaceId, Boolean empAttndStatus, String empAttndancedate, 
						   String empAttndanceInDateTime, String empAttndanceOutDateTime, String custAddress) {
		this.custId = custId;
		this.custName = custName;
		this.brId = brId;
		this.brName = brName;
		this.brCode = brCode;		
		this.empId = empid;
		this.employeeName = empName;
		this.custLogo = custLogo;
		this.appStatus = appStatus;
		this.empPresistedFaceId = empPresistedFaceId;
		this.empAttndStatus = empAttndStatus;
		this.empAttndancedate = empAttndancedate;
		this.empAttndanceInDateTime = empAttndanceInDateTime;
		this.empAttndanceOutDateTime = empAttndanceOutDateTime;
		this.custAddress = custAddress;
	}

	
	public String getCustAddress() {
		return custAddress;
	}


	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}


	public String getEmpAttndancedate() {
		return empAttndancedate;
	}


	public void setEmpAttndancedate(String empAttndancedate) {
		this.empAttndancedate = empAttndancedate;
	}


	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getBrCode() {
		return brCode;
	}

	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCustLogo() {
		return custLogo;
	}

	public void setCustLogo(String custLogo) {
		this.custLogo = custLogo;
	}

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public Boolean getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Boolean appStatus) {
		this.appStatus = appStatus;
	}

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

	public Boolean getEmpAttndStatus() {
		return empAttndStatus;
	}

	public void setEmpAttndStatus(Boolean empAttndStatus) {
		this.empAttndStatus = empAttndStatus;
	}

	public String getEmpAttndanceInDateTime() {
		return empAttndanceInDateTime;
	}

	public void setEmpAttndanceInDateTime(String empAttndanceInDateTime) {
		this.empAttndanceInDateTime = empAttndanceInDateTime;
	}

	public String getEmpAttndanceOutDateTime() {
		return empAttndanceOutDateTime;
	}

	public void setEmpAttndanceOutDateTime(String empAttndanceOutDateTime) {
		this.empAttndanceOutDateTime = empAttndanceOutDateTime;
	}

}
