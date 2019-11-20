package com.lng.dto.masters.employeeLeave;

import java.util.List;

import status.Status;

public class CustLeaveTrypeListDto {

	private Integer custId;
	
	private List<CustLeaveTypeDto> custLeaveTypeDtoList;
	
	public Status status;

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public List<CustLeaveTypeDto> getCustLeaveTypeDtoList() {
		return custLeaveTypeDtoList;
	}

	public void setCustLeaveTypeDtoList(List<CustLeaveTypeDto> custLeaveTypeDtoList) {
		this.custLeaveTypeDtoList = custLeaveTypeDtoList;
	}

}
