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
@Table(name = "tmemployeepic")
public class EmployeePic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employeePicId")
	private Integer employeePicId;
	
	@ManyToOne
	@JoinColumn(name = "refEmployeeId")
	private Employee employee;
	
	@Column(name ="employeePic")
	private byte[] employeePic;

	public Integer getEmployeePicId() {
		return employeePicId;
	}

	public void setEmployeePicId(Integer employeePicId) {
		this.employeePicId = employeePicId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public byte[] getEmployeePic() {
		return employeePic;
	}

	public void setEmployeePic(byte[] employeePic) {
		this.employeePic = employeePic;
	}

}
