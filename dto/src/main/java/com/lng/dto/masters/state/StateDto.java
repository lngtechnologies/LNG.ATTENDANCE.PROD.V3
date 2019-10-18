package com.lng.dto.masters.state;

import java.util.List;

public class StateDto {
	
	private Integer stateId;
	
	private Integer refCountryId;
	
	private String stateName;
	
	private String countryName;
	
	private  List<StateDto>stateDtoList;
	
	public Integer getStateId() {
		return stateId;
	}
	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getRefCountryId() {
		return refCountryId;
	}
	public void setRefCountryId(Integer refCountryId) {
		this.refCountryId = refCountryId;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public List<StateDto> getStateDtoList() {
		return stateDtoList;
	}
	public void setStateDtoList(List<StateDto> stateDtoList) {
		this.stateDtoList = stateDtoList;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	
}
