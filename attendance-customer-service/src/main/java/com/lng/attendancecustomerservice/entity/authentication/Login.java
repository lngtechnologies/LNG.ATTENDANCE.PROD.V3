package com.lng.attendancecustomerservice.entity.authentication;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ttlogin")
public class Login {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "loginId")
	private Integer loginId;

	@Column(name = "refCustId")
	private Integer refCustId;

	@Column(name = "loginName")
	private String loginName;

	@Column(name = "loginMobile")
	private String loginMobile;

	@Column(name = "loginPassword")
	@JsonIgnore
	private String loginPassword;

	@Column(name = "loginIsActive")
	private Boolean loginIsActive;

	@Column(name = "loginCreatedDate")
	private Date loginCreatedDate;
	
	@Column(name = "refEmpId")
	private Integer refEmpId;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginMobile() {
		return loginMobile;
	}

	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public Boolean getLoginIsActive() {
		return loginIsActive;
	}

	public void setLoginIsActive(Boolean loginIsActive) {
		this.loginIsActive = loginIsActive;
	}

	public Date getLoginCreatedDate() {
		return loginCreatedDate;
	}

	public void setLoginCreatedDate(Date loginCreatedDate) {
		this.loginCreatedDate = loginCreatedDate;
	}

	public Integer getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(Integer refEmpId) {
		this.refEmpId = refEmpId;
	}
	
}
