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
@Table(name = "ttempbranch")
public class EmployeeBranch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empBranchId")
	private Integer empBranchId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "refBranchId")
	private Branch branch;
	
	@Column(name = "branchFromDate")
	private Date branchFromDate;
	
	@Column(name = "branchToDate")
	private Date branchToDate;

	public Integer getEmpBranchId() {
		return empBranchId;
	}

	public void setEmpBranchId(Integer empBranchId) {
		this.empBranchId = empBranchId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Date getBranchFromDate() {
		return branchFromDate;
	}

	public void setBranchFromDate(Date branchFromDate) {
		this.branchFromDate = branchFromDate;
	}

	public Date getBranchToDate() {
		return branchToDate;
	}

	public void setBranchToDate(Date branchToDate) {
		this.branchToDate = branchToDate;
	}

}
