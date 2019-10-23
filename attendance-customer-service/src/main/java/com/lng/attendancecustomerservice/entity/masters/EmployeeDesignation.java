package com.lng.attendancecustomerservice.entity.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ttempdesignation")
public class EmployeeDesignation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empDesgnId")
	private Integer empDesgnId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "refDesignationId")
	private Designation designation;
	
	@Column(name = "empFromDate")
	private Date empFromDate;
	
	@Column(name = "empToDate")
	private Date empToDate;

	public Integer getEmpDesgnId() {
		return empDesgnId;
	}

	public void setEmpDesgnId(Integer empDesgnId) {
		this.empDesgnId = empDesgnId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Designation getDesignation() {
		return designation;
	}

	public void setDesignation(Designation designation) {
		this.designation = designation;
	}

	public Date getEmpFromDate() {
		return empFromDate;
	}

	public void setEmpFromDate(Date empFromDate) {
		this.empFromDate = empFromDate;
	}

	public Date getEmpToDate() {
		return empToDate;
	}

	public void setEmpToDate(Date empToDate) {
		this.empToDate = empToDate;
	}

}
