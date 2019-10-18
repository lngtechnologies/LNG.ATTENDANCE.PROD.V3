package com.lng.dto.country;

import java.util.List;

public class CountryDto {

	private Integer countryId;
	private String countryTelCode;
	private String countryName;
	private  List<CountryDto>countryDtoList;
	
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	public String getCountryTelCode() {
		return countryTelCode;
	}
	public void setCountryTelCode(String countryTelCode) {
		this.countryTelCode = countryTelCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public List<CountryDto> getCountryDtoList() {
		return countryDtoList;
	}
	public void setCountryDtoList(List<CountryDto> countryDtoList) {
		this.countryDtoList = countryDtoList;
	}
  

}
