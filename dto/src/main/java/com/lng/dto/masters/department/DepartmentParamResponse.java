package com.lng.dto.masters.department;

import java.util.List;

import status.Status;

public class DepartmentParamResponse {
	
	private List<DepartmentParam> departmentList;
	
	public Status  status;

	public List<DepartmentParam> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<DepartmentParam> departmentList) {
		this.departmentList = departmentList;
	}
	
	

}
