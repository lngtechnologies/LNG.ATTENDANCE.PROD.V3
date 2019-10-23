package com.lng.dto.masters.custEmployee;

import java.util.Date;

public class CustEmployeeDto {

	private Integer custId;

	private String empName;

	private String empMobile;

	private String empPassword;

	private String empGender;

	private Boolean empInService;

	private Integer brId;

	private Integer shiftId;

	private Integer deptartmentId;

	private Integer designationId;

	private Integer empTypeId;

	private Integer contractorId;

	private Integer reportingToId;

	private Date empJoiningDate;

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
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

	public String getEmpPassword() {
		return empPassword;
	}

	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
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

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public Integer getShiftId() {
		return shiftId;
	}

	public void setShiftId(Integer shiftId) {
		this.shiftId = shiftId;
	}

	public Integer getDeptartmentId() {
		return deptartmentId;
	}

	public void setDeptartmentId(Integer deptartmentId) {
		this.deptartmentId = deptartmentId;
	}

	public Integer getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
	}

	public Integer getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Integer empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Integer getContractorId() {
		return contractorId;
	}

	public void setContractorId(Integer contractorId) {
		this.contractorId = contractorId;
	}

	public Integer getReportingToId() {
		return reportingToId;
	}

	public void setReportingToId(Integer reportingToId) {
		this.reportingToId = reportingToId;
	}

	public Date getEmpJoiningDate() {
		return empJoiningDate;
	}

	public void setEmpJoiningDate(Date empJoiningDate) {
		this.empJoiningDate = empJoiningDate;
	}

}
