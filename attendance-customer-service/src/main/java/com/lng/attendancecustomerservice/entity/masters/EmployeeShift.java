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
@Table(name = "ttempshift")
public class EmployeeShift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name  = "empShiftId")
	private Integer empShiftId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "refShiftId")
	private Shift shift;
	
	@Column(name  = "shiftFromDate")
	private Date shiftFromDate;
	
	@Column(name  = "shiftToDate")
	private Date shiftToDate;

	public Integer getEmpShiftId() {
		return empShiftId;
	}

	public void setEmpShiftId(Integer empShiftId) {
		this.empShiftId = empShiftId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public Date getShiftFromDate() {
		return shiftFromDate;
	}

	public void setShiftFromDate(Date shiftFromDate) {
		this.shiftFromDate = shiftFromDate;
	}

	public Date getShiftToDate() {
		return shiftToDate;
	}

	public void setShiftToDate(Date shiftToDate) {
		this.shiftToDate = shiftToDate;
	}
	
}
