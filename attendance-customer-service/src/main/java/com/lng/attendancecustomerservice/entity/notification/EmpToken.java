package com.lng.attendancecustomerservice.entity.notification;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.lng.attendancecustomerservice.entity.masters.Employee;

@Entity
@Table(name = "ttemptoken")
public class EmpToken {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "empTokenId")
	private Integer empTokenId;
	
	@OneToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@Column(name = "token")
	private String token;
	
	@Column(name = "createdDate")
	private Date createdDate;
	
	@Column(name = "isActive")
	private Boolean isActive;

	public Integer getEmpTokenId() {
		return empTokenId;
	}

	public void setEmpTokenId(Integer empTokenId) {
		this.empTokenId = empTokenId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
