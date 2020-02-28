package com.lng.dto.employeeAppSetup;

import java.util.Date;

public class AttendanceParamDto {

	private Integer custId;

	private Integer empId;

	private  Date   dates;

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

	public Date getDates() {
		return dates;
	}

	public void setDates(Date dates) {
		this.dates = dates;
	}



}
