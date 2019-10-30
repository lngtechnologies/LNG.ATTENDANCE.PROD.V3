package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmindustry")
public class IndustryType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "industryId")
	private Integer industryId;
	
	@Column(name = "industryName")
	private String industryName;

	@Column(name = "industryIsActive")
	private Boolean industryIsActive;
	
	public Integer getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Integer industryId) {
		this.industryId = industryId;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public Boolean getIndustryIsActive() {
		return industryIsActive;
	}

	public void setIndustryIsActive(Boolean industryIsActive) {
		this.industryIsActive = industryIsActive;
	}

}
