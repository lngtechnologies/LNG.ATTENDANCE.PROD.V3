package com.lng.dto.masters.custEmployee;

import java.util.List;

import com.lng.dto.employeeAppSetup.EmployeeDto;
import com.lng.dto.employeeAppSetup.EmployeeDto2;

import status.Status;

public class CustEmployeeListResponse {

	private List<CustEmployeeDtoTwo> employyeList;
	
	public Status status;
	
	public  List<EmployeeDto2> data1;

	public List<CustEmployeeDtoTwo> getEmployyeList() {
		return employyeList;
	}

	public void setEmployyeList(List<CustEmployeeDtoTwo> employyeList) {
		this.employyeList = employyeList;
	}

	public List<EmployeeDto2> getData1() {
		return data1;
	}

	public void setData1(List<EmployeeDto2> employeeDtoList) {
		this.data1 = employeeDtoList;
	}
	
}
