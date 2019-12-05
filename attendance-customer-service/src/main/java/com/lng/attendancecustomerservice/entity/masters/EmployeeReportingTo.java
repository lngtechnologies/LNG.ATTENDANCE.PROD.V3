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
@Table(name = "ttempreportingto")
public class EmployeeReportingTo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empReportingToId")
	private Integer empReportingToId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@Column(name = "refEmpReportingToId")
	private Integer refEmpReportingToId;
	
	@Column(name = "empFromDate")
	private Date empFromDate;
	
	@Column(name = "empToDate")
	private Date empToDate;

	public Integer getEmpReportingToId() {
		return empReportingToId;
	}

	public void setEmpReportingToId(Integer empReportingToId) {
		this.empReportingToId = empReportingToId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getRefEmpReportingToId() {
		return refEmpReportingToId;
	}

	public void setRefEmpReportingToId(Integer refEmpReportingToId) {
		this.refEmpReportingToId = refEmpReportingToId;
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
