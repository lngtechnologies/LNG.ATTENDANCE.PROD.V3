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
	@Column(name = "userRightId")
	private Integer userRightId;
	
	@ManyToOne
	@JoinColumn(name = "refLoginId")
	private Login login;
	
	@ManyToOne
	@JoinColumn(name = "refModuleId")
	private Module module;

	public Integer getUserRightId() {
		return userRightId;
	}

	public void setUserRightId(Integer userRightId) {
		this.userRightId = userRightId;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

}
