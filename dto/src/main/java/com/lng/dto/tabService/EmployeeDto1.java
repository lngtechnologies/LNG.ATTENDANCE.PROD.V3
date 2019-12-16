package com.lng.dto.tabService;

public class EmployeeDto1 {

	private Integer  empId;

	private Integer refBrId;

	private Integer refCustId;

	private String empName;

	private String empMobile;

	private String empPresistedFaceId;

	private String empPicBlobPath;

	public Integer getRefBrId() {
		return refBrId;
	}

	public void setRefBrId(Integer refBrId) {
		this.refBrId = refBrId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
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

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

	public String getEmpPicBlobPath() {
		return empPicBlobPath;
	}

	public void setEmpPicBlobPath(String empPicBlobPath) {
		this.empPicBlobPath = empPicBlobPath;
	}



}
