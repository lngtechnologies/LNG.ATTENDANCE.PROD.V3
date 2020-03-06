package com.lng.dto.employeeAttendance;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeeAttendanceDto {

	private Integer empAttendanceId;

	private Integer refEmpId;

	private Date empAttendanceDate;

	private String empAttendanceInMode;

	private String empAttendanceOutMode;

	private Date empAttendanceInDatetime;

	private Date empAttendanceOutDatetime;

	private Date empAttendanceConsiderInDatetime;

	private Date empAttendanceConsiderOutDatetime;

	private BigDecimal empAttendanceInConfidence;

	private BigDecimal empAttendanceOutConfidence;

	private String empAttendanceInLatLong;

	private String empAttendanceOutLatLong;

	private String empAttendanceInLocation;

	private String  empAttendanceOutLocation;

	public Integer getEmpAttendanceId() {
		return empAttendanceId;
	}

	public void setEmpAttendanceId(Integer empAttendanceId) {
		this.empAttendanceId = empAttendanceId;
	}

	public Integer getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(Integer refEmpId) {
		this.refEmpId = refEmpId;
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

	public String getEmpAttendanceInLocation() {
		return empAttendanceInLocation;
	}

	public void setEmpAttendanceInLocation(String empAttendanceInLocation) {
		this.empAttendanceInLocation = empAttendanceInLocation;
	}

	public String getEmpAttendanceOutLocation() {
		return empAttendanceOutLocation;
	}

	public void setEmpAttendanceOutLocation(String empAttendanceOutLocation) {
		this.empAttendanceOutLocation = empAttendanceOutLocation;
	}

}
