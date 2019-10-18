package com.lng.dto.employee;

import java.util.Date;

public class EmployeeRegSaveDto {

	private Integer empId;

	private String empPicBlobPath;

	private String empPresistedFaceId;

	private String empDeviceName;

	private String empModelNumber;

	private String empAndriodVersion;

	private Boolean empAppStatus;

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmpPicBlobPath() {
		return empPicBlobPath;
	}

	public void setEmpPicBlobPath(String empPicBlobPath) {
		this.empPicBlobPath = empPicBlobPath;
	}

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

	public String getEmpDeviceName() {
		return empDeviceName;
	}

	public void setEmpDeviceName(String empDeviceName) {
		this.empDeviceName = empDeviceName;
	}

	public String getEmpModelNumber() {
		return empModelNumber;
	}

	public void setEmpModelNumber(String empModelNumber) {
		this.empModelNumber = empModelNumber;
	}

	public String getEmpAndriodVersion() {
		return empAndriodVersion;
	}

	public void setEmpAndriodVersion(String empAndriodVersion) {
		this.empAndriodVersion = empAndriodVersion;
	}

	public Boolean getEmpAppStatus() {
		return empAppStatus;
	}

	public void setEmpAppStatus(Boolean empAppStatus) {
		this.empAppStatus = empAppStatus;
	}

}
