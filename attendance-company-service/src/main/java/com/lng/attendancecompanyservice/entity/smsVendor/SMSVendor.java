package com.lng.attendancecompanyservice.entity.smsVendor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmsmsvendor")
public class SMSVendor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer smsVndrId;
	
	private String smsVndrName;
	
	private String smsVndrURL;
	
	private Date smsVndrExpiryDate;
	
	private Boolean smsVndrIsActive;

	public Integer getSmsVndrId() {
		return smsVndrId;
	}

	public void setSmsVndrId(Integer smsVndrId) {
		this.smsVndrId = smsVndrId;
	}

	public String getSmsVndrName() {
		return smsVndrName;
	}

	public void setSmsVndrName(String smsVndrName) {
		this.smsVndrName = smsVndrName;
	}

	public String getSmsVndrURL() {
		return smsVndrURL;
	}

	public void setSmsVndrURL(String smsVndrURL) {
		this.smsVndrURL = smsVndrURL;
	}

	public Date getSmsVndrExpiryDate() {
		return smsVndrExpiryDate;
	}

	public void setSmsVndrExpiryDate(Date smsVndrExpiryDate) {
		this.smsVndrExpiryDate = smsVndrExpiryDate;
	}

	public Boolean getSmsVndrIsActive() {
		return smsVndrIsActive;
	}

	public void setSmsVndrIsActive(Boolean smsVndrIsActive) {
		this.smsVndrIsActive = smsVndrIsActive;
	}

}
