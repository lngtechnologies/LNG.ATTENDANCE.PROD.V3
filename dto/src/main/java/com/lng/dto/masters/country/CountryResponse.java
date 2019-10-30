package com.lng.dto.masters.country;

import java.util.List;

import status.Status;

public class CountryResponse {
	public Status status;
	public List<CountryDto> data1;
	public CountryDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<CountryDto> getData1() {
		return data1;
	}
	public void setData1(List<CountryDto> data1) {
		this.data1 = data1;
	}
	public CountryDto getData() {
		return data;
	}
	public void setData(CountryDto data) {
		this.data = data;
	}

}