package com.lng.dto.customer;

import java.util.List;

import status.Status;

public class CustomerListResponse {

	private List<CustomerDtoTwo> dataList;
	public Status status;
	
	public List<CustomerDtoTwo> getDataList() {
		return dataList;
	}
	public void setDataList(List<CustomerDtoTwo> dataList) {
		this.dataList = dataList;
	}
	
	
	
	
	
}
