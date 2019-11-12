package com.lng.dto.empAttendance;

import java.math.BigDecimal;
import java.util.Date;

public class EmpAttendanceDto {

	private Integer empAttendanceId;

	private  Integer refEmpId;

	private  String empAttendanceMode;

	private Date empAttendanceDatetime;

	private Date  empAttendanceConsiderDatetime;

	private BigDecimal  empAttendanceConfidence;

	private String  empAttendanceLatLong;

	private Boolean  empAttendanceWithinBeacon;
	
	private  String  empName;
	
	private String   shiftStart; 
	
	

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

	public String getEmpAttendanceLatLong() {
		return empAttendanceLatLong;
	}

	public void setEmpAttendanceLatLong(String empAttendanceLatLong) {
		this.empAttendanceLatLong = empAttendanceLatLong;
	}

	public Boolean getEmpAttendanceWithinBeacon() {
		return empAttendanceWithinBeacon;
	}

	public void setEmpAttendanceWithinBeacon(Boolean empAttendanceWithinBeacon) {
		this.empAttendanceWithinBeacon = empAttendanceWithinBeacon;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}






  


}
