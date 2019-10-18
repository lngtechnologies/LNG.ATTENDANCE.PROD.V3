package com.lng.dto.masters.country;

import java.util.List;

import status.Status;

public class CountryResponse {
	public Status status;
	public List<CountryDto> data;

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<CountryDto> getData() {
		return data;
	}
	public void setData(List<CountryDto> data) {
		this.data = data;
	}	



}