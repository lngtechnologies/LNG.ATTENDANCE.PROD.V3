package com.lng.attendancecustomerservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmcountry")
public class Country {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "countryId")
	private Integer countryId;
	
	@Column(name = "countryTelCode")
	private String countryTelCode;
	
	@Column(name = "countryName")
	private String countryName;
	
	@Column(name = "countryIsActive")
	private Boolean countryIsActive;

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

	public Boolean getCountryIsActive() {
		return countryIsActive;
	}

	public void setCountryIsActive(Boolean countryIsActive) {
		this.countryIsActive = countryIsActive;
	}

	
}
