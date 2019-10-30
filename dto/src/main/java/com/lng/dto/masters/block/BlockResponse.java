package com.lng.dto.masters.block;

import java.util.List;

import status.Status;

public class BlockResponse {
	public Status status;
	public List<BlockDto> data1;
	public BlockDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<BlockDto> getData1() {
		return data1;
	}
	public void setData1(List<BlockDto> data1) {
		this.data1 = data1;
	}
	public BlockDto getData() {
		return data;
	}
	public void setData(BlockDto data) {
		this.data = data;
	}
	
	
}
