package com.lng.attendancecustomerservice.entity.authentication;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	public String loginPassword;

	@Column(name = "loginIsActive")
	private Integer  isActive;

	@Column(name = "loginCreatedDate")
	private Date  createdDate;
	
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

	public String getLoginMobile() {
		return loginMobile;
	}
	
	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

	public Integer getIsActive() {
		return isActive;
	}
	
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String username) {
		this.loginName = username;
	}
	
	public String getLoginPassword() {
		return loginPassword;
	}
	
	public void setLoginPassword(String password) {
		this.loginPassword = password;
	}
}
