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
@Table(name = "ttempattendance")
public class EmployeeAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empAttendanceId")
	private Integer empAttendanceId;

	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;

	@Column(name = "empAttendanceMode")
	private String empAttendanceMode;

	@Column(name = "empAttendanceDatetime")
	private Date empAttendanceDatetime;

	@Column(name = "empAttendanceConsiderDatetime")
	private Date empAttendanceConsiderDatetime;

	@Column(name = "empAttendanceConfidence")
	private BigDecimal empAttendanceConfidence;

	@Column(name = "empAttendanceLatitude")
	private Double empAttendanceLatitude;

	@Column(name = "empAttendanceLongitude")
	private Double empAttendanceLongitude;


	public Integer getEmpAttendanceId() {
		return empAttendanceId;
	}

	public void setEmpAttendanceId(Integer empAttendanceId) {
		this.empAttendanceId = empAttendanceId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmpAttendanceMode() {
		return empAttendanceMode;
	}

	public void setEmpAttendanceMode(String empAttendanceMode) {
		this.empAttendanceMode = empAttendanceMode;
	}

	public Date getEmpAttendanceDatetime() {
		return empAttendanceDatetime;
	}

	public void setEmpAttendanceDatetime(Date empAttendanceDatetime) {
		this.empAttendanceDatetime = empAttendanceDatetime;
	}

	public Date getEmpAttendanceConsiderDatetime() {
		return empAttendanceConsiderDatetime;
	}

	public void setEmpAttendanceConsiderDatetime(Date empAttendanceConsiderDatetime) {
		this.empAttendanceConsiderDatetime = empAttendanceConsiderDatetime;
	}

	public BigDecimal getEmpAttendanceConfidence() {
		return empAttendanceConfidence;
	}

	public void setEmpAttendanceConfidence(BigDecimal empAttendanceConfidence) {
		this.empAttendanceConfidence = empAttendanceConfidence;
	}

	public Double getEmpAttendanceLatitude() {
		return empAttendanceLatitude;
	}

	public void setEmpAttendanceLatitude(Double empAttendanceLatitude) {
		this.empAttendanceLatitude = empAttendanceLatitude;
	}

	public Double getEmpAttendanceLongitude() {
		return empAttendanceLongitude;
	}

	public void setEmpAttendanceLongitude(Double empAttendanceLongitude) {
		this.empAttendanceLongitude = empAttendanceLongitude;
	}

}
