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
@Table(name = "ttempleave")
public class EmpLeave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empLeaveId")
	private Integer empLeaveId;
	
	@ManyToOne
    @JoinColumn(name = "refEmpId")
	private Employee employee;
	
	
	@ManyToOne
    @JoinColumn(name = "refCustLeaveId")
	private CustLeave custLeave;
	
	@Column(name = "empLeaveAppliedDatetime")
	private Date  empLeaveAppliedDatetime;
	
	@Column(name = "empLeaveFrom")
	private Date empLeaveFrom;
	
	@Column(name = "empLeaveTo")
	private Date empLeaveTo;
	
	@Column(name = "empLeaveDaysCount")
	private Integer empLeaveDaysCount;
	
	@Column(name = "empLeaveRemarks")
	private String empLeaveRemarks;
	
	@Column(name = "empLeaveStatus")
	private String empLeaveStatus;
	
	@Column(name = "empLeaveRejectionRemarks")
	private String empLeaveRejectionRemarks;
	
	@Column(name = "empLeaveAppRejBy")
	private Integer empLeaveAppRejBy;
	
	@Column(name = "empLeaveStatusUpdatedDatetime")
	private Date empLeaveStatusUpdatedDatetime;
	
	@Column(name = "empLeaveRequestForCancellation")
	private Boolean empLeaveRequestForCancellation;

	public Integer getEmpLeaveId() {
		return empLeaveId;
	}

	public void setEmpLeaveId(Integer empLeaveId) {
		this.empLeaveId = empLeaveId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public CustLeave getCustLeave() {
		return custLeave;
	}

	public void setCustLeave(CustLeave custLeave) {
		this.custLeave = custLeave;
	}

	public Date getEmpLeaveAppliedDatetime() {
		return empLeaveAppliedDatetime;
	}

	public void setEmpLeaveAppliedDatetime(Date empLeaveAppliedDatetime) {
		this.empLeaveAppliedDatetime = empLeaveAppliedDatetime;
	}

	public Date getEmpLeaveFrom() {
		return empLeaveFrom;
	}

	public void setEmpLeaveFrom(Date empLeaveFrom) {
		this.empLeaveFrom = empLeaveFrom;
	}

	public Date getEmpLeaveTo() {
		return empLeaveTo;
	}

	public void setEmpLeaveTo(Date empLeaveTo) {
		this.empLeaveTo = empLeaveTo;
	}

	public Integer getEmpLeaveDaysCount() {
		return empLeaveDaysCount;
	}

	public void setEmpLeaveDaysCount(Integer empLeaveDaysCount) {
		this.empLeaveDaysCount = empLeaveDaysCount;
	}

	public String getEmpLeaveRemarks() {
		return empLeaveRemarks;
	}

	public void setEmpLeaveRemarks(String empLeaveRemarks) {
		this.empLeaveRemarks = empLeaveRemarks;
	}

	public String getEmpLeaveStatus() {
		return empLeaveStatus;
	}

	public void setEmpLeaveStatus(String empLeaveStatus) {
		this.empLeaveStatus = empLeaveStatus;
	}

	public String getEmpLeaveRejectionRemarks() {
		return empLeaveRejectionRemarks;
	}

	public void setEmpLeaveRejectionRemarks(String empLeaveRejectionRemarks) {
		this.empLeaveRejectionRemarks = empLeaveRejectionRemarks;
	}

	public Integer getEmpLeaveAppRejBy() {
		return empLeaveAppRejBy;
	}

	public void setEmpLeaveAppRejBy(Integer empLeaveAppRejBy) {
		this.empLeaveAppRejBy = empLeaveAppRejBy;
	}

	public Date getEmpLeaveStatusUpdatedDatetime() {
		return empLeaveStatusUpdatedDatetime;
	}

	public void setEmpLeaveStatusUpdatedDatetime(Date empLeaveStatusUpdatedDatetime) {
		this.empLeaveStatusUpdatedDatetime = empLeaveStatusUpdatedDatetime;
	}

	public Boolean getEmpLeaveRequestForCancellation() {
		return empLeaveRequestForCancellation;
	}

	public void setEmpLeaveRequestForCancellation(Boolean empLeaveRequestForCancellation) {
		this.empLeaveRequestForCancellation = empLeaveRequestForCancellation;
	}
	
    
}
