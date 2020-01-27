package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.lng.attendancecompanyservice.entity.userModule.Module;

@Entity
@Table(name = "ttuserright")
public class UserRight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userRightId;

	private Integer refLoginId;

	private Integer refModuleId;

	public Integer getUserRightId() {
		return userRightId;
	}

	public void setUserRightId(Integer userRightId) {
		this.userRightId = userRightId;
	}

	public Integer getRefLoginId() {
		return refLoginId;
	}

	public void setRefLoginId(Integer refLoginId) {
		this.refLoginId = refLoginId;
	}

	public Integer getRefModuleId() {
		return refModuleId;
	}

	public void setRefModuleId(Integer refModuleId) {
		this.refModuleId = refModuleId;
	}

}