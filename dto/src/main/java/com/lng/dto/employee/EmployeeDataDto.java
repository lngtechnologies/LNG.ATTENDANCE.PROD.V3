package com.lng.dto.employee;

public class EmployeeDataDto {
	
	private Integer custId;

	private Integer empId;
	
	private String employeeName;
	
	private String faceListId;
	
	private String empAppSetupStatus;
	
	public EmployeeDataDto(Integer id, Integer empid, String empName, String faceListId, String empAppSetupStatus) {
		this.custId = id;
		this.empId = empid;
		this.employeeName = empName;
		this.faceListId = faceListId;
		this.empAppSetupStatus = empAppSetupStatus;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
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

	public String getFaceListId() {
		return faceListId;
	}

	public void setFaceListId(String faceListId) {
		this.faceListId = faceListId;
	}

	public String getEmpAppSetupStatus() {
		return empAppSetupStatus;
	}

	public void setEmpAppSetupStatus(String empAppSetupStatus) {
		this.empAppSetupStatus = empAppSetupStatus;
	}
	

}
