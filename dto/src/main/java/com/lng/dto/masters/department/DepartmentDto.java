package com.lng.dto.masters.department;

public class DepartmentDto {
	private Integer deptId;
	private Integer refCustId;
	private String deptName;
	private Boolean deptIsActive;
    private String  custName;
    private  Integer brId;
	
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getRefCustId() {
		return refCustId;
	}
	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public Boolean getDeptIsActive() {
		return deptIsActive;
	}
	public void setDeptIsActive(Boolean deptIsActive) {
		this.deptIsActive = deptIsActive;
	}
	public Integer getBrId() {
		return brId;
	}
	public void setBrId(Integer brId) {
		this.brId = brId;
	}


}
