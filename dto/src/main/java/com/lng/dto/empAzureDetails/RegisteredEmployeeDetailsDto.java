package com.lng.dto.empAzureDetails;

import java.util.Date;

public class RegisteredEmployeeDetailsDto {

	private Integer empId;
	
	private String empName;
	
	private String empMobile;
	
	private String empGender;
	
	private Boolean empInService;
	
	private Date empJoiningDate;
	
	private String empPresistedFaceId;

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpMobile() {
		return empMobile;
	}

	public void setEmpMobile(String empMobile) {
		this.empMobile = empMobile;
	}

	public String getEmpGender() {
		return empGender;
	}

	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}

	public Boolean getEmpInService() {
		return empInService;
	}

	public void setEmpInService(Boolean empInService) {
		this.empInService = empInService;
	}

	public Date getEmpJoiningDate() {
		return empJoiningDate;
	}

	public void setEmpJoiningDate(Date empJoiningDate) {
		this.empJoiningDate = empJoiningDate;
	}

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

}
