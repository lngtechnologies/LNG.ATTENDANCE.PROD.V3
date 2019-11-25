package com.lng.attendancecustomerservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tmdepartment")
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "deptId")
	private Integer deptId;
	
	@Column(name = "deptName")
	private String deptName;
	
	@Column(name = "deptIsActive")
	private Boolean deptIsActive;
	
	@ManyToOne
	@JoinColumn(name = "refCustId")
	private Customer customer;
	
	
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Boolean getDeptIsActive() {
		return deptIsActive;
	}
	public void setDeptIsActive(Boolean deptIsActive) {
		this.deptIsActive = deptIsActive;
	}

}
