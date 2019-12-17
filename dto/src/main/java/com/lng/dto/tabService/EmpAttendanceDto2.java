package com.lng.dto.tabService;

import java.math.BigDecimal;
import java.util.Date;

public class EmpAttendanceDto2 {
	
	private Integer empAttendanceId;

	private Integer refEmpId;

	private Date empAttendanceDate;

	private String empAttendanceMode;

	private Date empAttendanceDatetime;

	private Date empAttendanceConsiderDatetime;

	private BigDecimal empAttendanceConfidence;

	private String empAttendanceLatLong;

	private String  Flag;

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

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	}
	
	

}
