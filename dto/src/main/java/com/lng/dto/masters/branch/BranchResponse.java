package com.lng.dto.masters.branch;

import java.util.List;

import status.Status;

public class BranchResponse {
	
	  public Status status; 
	  public List<BranchDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<BranchDto> getData() {
		return data;
	}
	public void setData(List<BranchDto> data) {
		this.data = data;
	}
	  
	  

}
