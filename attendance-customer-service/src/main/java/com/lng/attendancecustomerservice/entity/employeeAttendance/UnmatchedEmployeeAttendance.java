package com.lng.attendancecustomerservice.entity.employeeAttendance;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.lng.attendancecustomerservice.entity.masters.Employee;

@Entity
@Table(name = "ttunmatchedempattendance")
public class UnmatchedEmployeeAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empUnmatchedAttendanceId")
	private Integer empUnmatchedAttendanceId;

	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;

	@Column(name = "empAttendanceDate")
	private Date empAttendanceDate;

	@Column(name = "empAttendanceInMode")
	private String empAttendanceInMode;

	@Column(name = "empAttendanceOutMode")
	private String empAttendanceOutMode;

	@Column(name = "empAttendanceInDatetime")
	private Date empAttendanceInDatetime;

	@Column(name = "empAttendanceOutDatetime")
	private Date empAttendanceOutDatetime;

	@Column(name = "empAttendanceConsiderInDatetime")
	private Date empAttendanceConsiderInDatetime;

	@Column(name = "empAttendanceConsiderOutDatetime")
	private Date empAttendanceConsiderOutDatetime;

	@Column(name = "empAttendanceInConfidence")
	private BigDecimal empAttendanceInConfidence;

	@Column(name = "empAttendanceOutConfidence")
	private BigDecimal empAttendanceOutConfidence;

	@Column(name = "empAttendanceInLatLong")
	private String empAttendanceInLatLong;

	@Column(name = "empAttendanceOutLatLong")
	private String empAttendanceOutLatLong;


	public Integer getEmpUnmatchedAttendanceId() {
		return empUnmatchedAttendanceId;
	}

	public void setEmpUnmatchedAttendanceId(Integer empUnmatchedAttendanceId) {
		this.empUnmatchedAttendanceId = empUnmatchedAttendanceId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Date getEmpAttendanceDate() {
		return empAttendanceDate;
	}

	public void setEmpAttendanceDate(Date empAttendanceDate) {
		this.empAttendanceDate = empAttendanceDate;
	}

	public String getEmpAttendanceInMode() {
		return empAttendanceInMode;
	}

	public void setEmpAttendanceInMode(String empAttendanceInMode) {
		this.empAttendanceInMode = empAttendanceInMode;
	}

	public String getEmpAttendanceOutMode() {
		return empAttendanceOutMode;
	}

	public void setEmpAttendanceOutMode(String empAttendanceOutMode) {
		this.empAttendanceOutMode = empAttendanceOutMode;
	}

	public Date getEmpAttendanceInDatetime() {
		return empAttendanceInDatetime;
	}

	public void setEmpAttendanceInDatetime(Date empAttendanceInDatetime) {
		this.empAttendanceInDatetime = empAttendanceInDatetime;
	}

	public Date getEmpAttendanceOutDatetime() {
		return empAttendanceOutDatetime;
	}

	public void setEmpAttendanceOutDatetime(Date empAttendanceOutDatetime) {
		this.empAttendanceOutDatetime = empAttendanceOutDatetime;
	}

	public Date getEmpAttendanceConsiderInDatetime() {
		return empAttendanceConsiderInDatetime;
	}

	public void setEmpAttendanceConsiderInDatetime(Date empAttendanceConsiderInDatetime) {
		this.empAttendanceConsiderInDatetime = empAttendanceConsiderInDatetime;
	}

	public Date getEmpAttendanceConsiderOutDatetime() {
		return empAttendanceConsiderOutDatetime;
	}

	public void setEmpAttendanceConsiderOutDatetime(Date empAttendanceConsiderOutDatetime) {
		this.empAttendanceConsiderOutDatetime = empAttendanceConsiderOutDatetime;
	}

	public BigDecimal getEmpAttendanceInConfidence() {
		return empAttendanceInConfidence;
	}

	public void setEmpAttendanceInConfidence(BigDecimal empAttendanceInConfidence) {
		this.empAttendanceInConfidence = empAttendanceInConfidence;
	}

	public BigDecimal getEmpAttendanceOutConfidence() {
		return empAttendanceOutConfidence;
	}

	public void setEmpAttendanceOutConfidence(BigDecimal empAttendanceOutConfidence) {
		this.empAttendanceOutConfidence = empAttendanceOutConfidence;
	}

	public String getEmpAttendanceInLatLong() {
		return empAttendanceInLatLong;
	}

	public void setEmpAttendanceInLatLong(String empAttendanceInLatLong) {
		this.empAttendanceInLatLong = empAttendanceInLatLong;
	}

	public String getEmpAttendanceOutLatLong() {
		return empAttendanceOutLatLong;
	}

	public void setEmpAttendanceOutLatLong(String empAttendanceOutLatLong) {
		this.empAttendanceOutLatLong = empAttendanceOutLatLong;
	}
}
