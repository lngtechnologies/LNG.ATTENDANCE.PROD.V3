package com.lng.attendancecustomerservice.entity.masters;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ttlogindataright")
public class LoginDataRight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer loginDataRightId;
	
	
	private Integer refLoginId;
	
	private Integer refBrId;

	public Integer getLoginDataRightId() {
		return loginDataRightId;
	}

	public void setLoginDataRightId(Integer loginDataRightId) {
		this.loginDataRightId = loginDataRightId;
	}

	public Integer getRefLoginId() {
		return refLoginId;
	}

	public void setRefLoginId(Integer refLoginId) {
		this.refLoginId = refLoginId;
	}

	public Integer getRefBrId() {
		return refBrId;
	}

	public void setRefBrId(Integer refBrId) {
		this.refBrId = refBrId;
	}

	
}
