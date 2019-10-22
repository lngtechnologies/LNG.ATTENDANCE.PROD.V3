package com.lng.dto.employee;

public class EmployeeDataDto {

	private Integer custId;

	private String custName;

	private String brCode;

	private Integer empId;

	private String employeeName;

	private String empPresistedFaceId;

	private Boolean empAppSetupStatus;

	private String custLogo;

	//private Boolean empAppSetupStatus;

	public EmployeeDataDto(Integer custId, String custName, String brCode, Integer empid, String empName, String empPresistedFaceId, Boolean empAppSetupStatus, String custLogo) {
		this.custId = custId;
		this.custName = custName;
		this.brCode = brCode;		
		this.empId = empid;
		this.employeeName = empName;		
		this.empPresistedFaceId = empPresistedFaceId;
		this.empAppSetupStatus = empAppSetupStatus;
		this.custLogo = custLogo;
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

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

	public Boolean getEmpAppSetupStatus() {
		return empAppSetupStatus;
	}

	public void setEmpAppSetupStatus(Boolean empAppSetupStatus) {
		this.empAppSetupStatus = empAppSetupStatus;
	}


}
