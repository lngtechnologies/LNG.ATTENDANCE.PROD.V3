package com.lng.dto.masters.branch;

import java.util.List;

import status.Status;

public class BranchResponse {
	
	  public Status status; 
	  public BranchDto data;
	  public List<BranchDto> data1;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public BranchDto getData() {
		return data;
	}
	public void setData(BranchDto data) {
		this.data = data;
	}
	public List<BranchDto> getData1() {
		return data1;
	}
	public void setData1(List<BranchDto> data1) {
		this.data1 = data1;
	}

}
