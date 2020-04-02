package com.lng.attendancetabservice.entity;

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
@Table(name = "ttEmpFailedAttendance")
public class EmpFailedAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empFailedAttendanceId")
	private Integer empFailedAttendanceId;

	@ManyToOne
	@JoinColumn(name = "refCustId")
	private Customer refCustId;

	@ManyToOne
	@JoinColumn(name = "refBrId")
	private Branch refBrId;

	@Column(name = "empAttendanceFlag")
	private String empAttendanceFlag;

	@Column(name = "empAttendanceDatetime")
	private Date empAttendanceDatetime;

	@Column(name = "employeePicture")
	private byte[] employeePicture;
	

	public Integer getEmpFailedAttendanceId() {
		return empFailedAttendanceId;
	}

	public void setEmpFailedAttendanceId(Integer empFailedAttendanceId) {
		this.empFailedAttendanceId = empFailedAttendanceId;
	}

	public Customer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Customer refCustId) {
		this.refCustId = refCustId;
	}

	public Branch getRefBrId() {
		return refBrId;
	}

	public void setRefBrId(Branch refBrId) {
		this.refBrId = refBrId;
	}

	public String getEmpAttendanceFlag() {
		return empAttendanceFlag;
	}

	public void setEmpAttendanceFlag(String empAttendanceFlag) {
		this.empAttendanceFlag = empAttendanceFlag;
	}

	public Date getEmpAttendanceDatetime() {
		return empAttendanceDatetime;
	}

	public void setEmpAttendanceDatetime(Date empAttendanceDatetime) {
		this.empAttendanceDatetime = empAttendanceDatetime;
	}

	public byte[] getEmployeePicture() {
		return employeePicture;
	}

	public void setEmployeePicture(byte[] employeePicture) {
		this.employeePicture = employeePicture;
	}

}
