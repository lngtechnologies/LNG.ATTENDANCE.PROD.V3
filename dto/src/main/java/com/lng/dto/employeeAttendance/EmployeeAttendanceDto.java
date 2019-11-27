package com.lng.dto.employeeAttendance;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeeAttendanceDto {

	private Integer empAttendanceId;
	
	private Integer refEmpId;
	
	private String empAttendanceMode;
	
	private Date empAttendanceDatetime;
	
	private Date empAttendanceConsiderDatetime;
	
	private BigDecimal empAttendanceConfidence;
	
	private Double empAttendanceLatitude;
	
	private Double empAttendanceLongitude;
	
	//private Boolean empAttendanceWithinBeacon;

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
