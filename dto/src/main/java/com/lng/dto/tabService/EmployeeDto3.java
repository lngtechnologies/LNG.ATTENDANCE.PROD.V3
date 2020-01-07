package com.lng.dto.tabService;

import java.sql.Time;

public class EmployeeDto3 {

	private String shiftType;

	private Integer empId;

	private String  shiftStart;

	private String    shiftEnd;
	
	private Integer custId;
	
	private String outPermissibleTime; 

	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}

	public String getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(String shiftEnd) {
		this.shiftEnd = shiftEnd;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getOutPermissibleTime() {
		return outPermissibleTime;
	}

	public void setOutPermissibleTime(String outPermissibleTime) {
		this.outPermissibleTime = outPermissibleTime;
	}



}
