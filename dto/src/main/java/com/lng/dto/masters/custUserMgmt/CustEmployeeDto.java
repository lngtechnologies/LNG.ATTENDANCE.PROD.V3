package com.lng.dto.masters.custUserMgmt;

import java.util.Date;

public class CustEmployeeDto {

	private Integer empId;

	private Integer custId;

	private String empName;

	private String empMobile;

	private String empPassword;

	private String empGender;

	private Boolean empInService;
	
	private Boolean	empIsSupervisor_Manager;

	private Integer brId;

	private Integer shiftId;

	private Integer departmentId;

	private Integer designationId;

	private Integer empTypeId;

	private Integer contractorId;

	private Integer empReportingToId;
	
	private String dayOfWeek;

	private Date empJoiningDate;

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

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

	public Boolean getEmpIsSupervisor_Manager() {
		return empIsSupervisor_Manager;
	}

	public void setEmpIsSupervisor_Manager(Boolean empIsSupervisor_Manager) {
		this.empIsSupervisor_Manager = empIsSupervisor_Manager;
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

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
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

	public Integer getEmpReportingToId() {
		return empReportingToId;
	}

	public void setEmpReportingToId(Integer empReportingToId) {
		this.empReportingToId = empReportingToId;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getEmpJoiningDate() {
		return empJoiningDate;
	}

	public void setEmpJoiningDate(Date empJoiningDate) {
		this.empJoiningDate = empJoiningDate;
	}
}
