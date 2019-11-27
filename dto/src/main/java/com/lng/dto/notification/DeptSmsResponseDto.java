package com.lng.dto.notification;

import java.util.List;

import status.Status;

public class DeptSmsResponseDto {

	private List<DepartmentDto> departmentDtoList;
	
	public Status status;

	public List<DepartmentDto> getDepartmentDtoList() {
		return departmentDtoList;
	}

	public void setDepartmentDtoList(List<DepartmentDto> departmentDtoList) {
		this.departmentDtoList = departmentDtoList;
	}

}
