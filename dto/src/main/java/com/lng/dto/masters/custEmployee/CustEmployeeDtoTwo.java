package com.lng.dto.masters.custEmployee;

import java.util.Date;
import java.util.List;

public class CustEmployeeDtoTwo {


	private Integer empId;

	private Integer custId;

	private String empName;

	private String empMobile;

	private String empGender;

	private Boolean empInService;

	private Integer brId;

	private String brName;

	private Integer shiftId;

	private String shiftName;

	private Integer departmentId;

	private String deptName;

	private Integer designationId;

	private Boolean empIsSupervisor_Manager;

	private String designationName;

	private Integer empTypeId;

	private String empType;

	private Integer contractorId;

	private String contractorName;

	private String dayOfWeek;

	private Integer empReportingToId;

	private String empReportingTo;

	private Date empJoiningDate;

	private Date employeeBranchFromDate;

	private Date employeeDepartmentFromDate;

	private Date employeeDesignationFromDate;

	private Date employeeShiftFromDate;

	private Date empWeeklyOffDayFromDate;
	
	private List<EmpBlockMapDto> empBlockMapDtoList;

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

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}



	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Integer getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
	}

	public String getDesignationName() {
		return designationName;
	}

	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}

	public Integer getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Integer empTypeId) {
		this.empTypeId = empTypeId;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public Integer getContractorId() {
		return contractorId;
	}

	public void setContractorId(Integer contractorId) {
		this.contractorId = contractorId;
	}

	public String getContractorName() {
		return contractorName;
	}

	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}

	public Integer getEmpReportingToId() {
		return empReportingToId;
	}

	public void setEmpReportingToId(Integer empReportingToId) {
		this.empReportingToId = empReportingToId;
	}

	public String getEmpReportingTo() {
		return empReportingTo;
	}

	public void setEmpReportingTo(String empReportingTo) {
		this.empReportingTo = empReportingTo;
	}

	public Date getEmpJoiningDate() {
		return empJoiningDate;
	}

	public void setEmpJoiningDate(Date empJoiningDate) {
		this.empJoiningDate = empJoiningDate;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Boolean getEmpIsSupervisor_Manager() {
		return empIsSupervisor_Manager;
	}

	public void setEmpIsSupervisor_Manager(Boolean empIsSupervisor_Manager) {
		this.empIsSupervisor_Manager = empIsSupervisor_Manager;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Date getEmployeeBranchFromDate() {
		return employeeBranchFromDate;
	}

	public void setEmployeeBranchFromDate(Date employeeBranchFromDate) {
		this.employeeBranchFromDate = employeeBranchFromDate;
	}

	public Date getEmployeeDepartmentFromDate() {
		return employeeDepartmentFromDate;
	}

	public void setEmployeeDepartmentFromDate(Date employeeDepartmentFromDate) {
		this.employeeDepartmentFromDate = employeeDepartmentFromDate;
	}

	public Date getEmployeeDesignationFromDate() {
		return employeeDesignationFromDate;
	}

	public void setEmployeeDesignationFromDate(Date employeeDesignationFromDate) {
		this.employeeDesignationFromDate = employeeDesignationFromDate;
	}

	public Date getEmployeeShiftFromDate() {
		return employeeShiftFromDate;
	}

	public void setEmployeeShiftFromDate(Date employeeShiftFromDate) {
		this.employeeShiftFromDate = employeeShiftFromDate;
	}

	public Date getEmpWeeklyOffDayFromDate() {
		return empWeeklyOffDayFromDate;
	}

	public void setEmpWeeklyOffDayFromDate(Date empWeeklyOffDayFromDate) {
		this.empWeeklyOffDayFromDate = empWeeklyOffDayFromDate;
	}

	public List<EmpBlockMapDto> getEmpBlockMapDtoList() {
		return empBlockMapDtoList;
	}

	public void setEmpBlockMapDtoList(List<EmpBlockMapDto> empBlockMapDtoList) {
		this.empBlockMapDtoList = empBlockMapDtoList;
	}

}
