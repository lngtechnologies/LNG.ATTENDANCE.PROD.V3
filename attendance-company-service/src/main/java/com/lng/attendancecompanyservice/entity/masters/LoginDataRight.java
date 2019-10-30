package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ttlogindataright")
public class LoginDataRight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "loginDataRightId")
	private Integer loginDataRightId;
	
	@ManyToOne
	@JoinColumn(name = "refLoginId")
	private Login login;
	
	@ManyToOne
	@JoinColumn(name = "refBrId")
	private Branch branch;

	
	public Integer getLoginDataRightId() {
		return loginDataRightId;
	}

	public void setLoginDataRightId(Integer loginDataRightId) {
		this.loginDataRightId = loginDataRightId;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	
}
