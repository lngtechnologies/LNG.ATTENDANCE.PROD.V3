package com.lng.dto.masters.block;

import java.util.List;

import status.Status;

public class BlockResponse {
	public Status status;
	public List<BlockDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<BlockDto> getData() {
		return data;
	}
	public void setData(List<BlockDto> data) {
		this.data = data;
	}	
	
	

}
