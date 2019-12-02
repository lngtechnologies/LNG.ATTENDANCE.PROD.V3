package com.lng.attendancecompanyservice.entity.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmBeacon")
public class Beacon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "beaconId")
	private Integer beaconId;

	@Column(name = "beaconCode")
	private String beaconCode;

	@Column(name = "beaconCreatedDate")
	private Date beaconCreatedDate;
	
	@Column(name = "beaconIsActive")
	private Boolean beaconIsActive;

	public Integer getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(Integer beaconId) {
		this.beaconId = beaconId;
	}

	public String getBeaconCode() {
		return beaconCode;
	}

	public void setBeaconCode(String beaconCode) {
		this.beaconCode = beaconCode;
	}

	public Date getBeaconCreatedDate() {
		return beaconCreatedDate;
	}

	public void setBeaconCreatedDate(Date beaconCreatedDate) {
		this.beaconCreatedDate = beaconCreatedDate;
	}

	public Boolean getBeaconIsActive() {
		return beaconIsActive;
	}

	public void setBeaconIsActive(Boolean beaconIsActive) {
		this.beaconIsActive = beaconIsActive;
	}
	
}
