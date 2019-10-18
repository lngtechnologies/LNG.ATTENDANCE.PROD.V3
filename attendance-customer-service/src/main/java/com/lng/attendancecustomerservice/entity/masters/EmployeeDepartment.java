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
@Table(name="ttempdept")
public class EmployeeDepartment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empDeptId")
	private Integer empDeptId;
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	@ManyToOne
	@JoinColumn(name = "refDeptId")
	private Department department;
	@Column(name = "empFormDate")
	private Date empFormDate;
	@Column(name = "empToDate")
	private Date  empToDate;
	public Integer getEmpDeptId() {
		return empDeptId;
	}
	public void setEmpDeptId(Integer empDeptId) {
		this.empDeptId = empDeptId;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Date getEmpFormDate() {
		return empFormDate;
	}
	public void setEmpFormDate(Date empFormDate) {
		this.empFormDate = empFormDate;
	}
	public Date getEmpToDate() {
		return empToDate;
	}
	public void setEmpToDate(Date empToDate) {
		this.empToDate = empToDate;
	}

}
