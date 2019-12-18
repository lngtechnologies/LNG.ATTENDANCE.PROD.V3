package com.lng.dto.tabService;

import java.math.BigDecimal;
import java.util.Date;

public class EmpAttendanceDto1 {

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

	private  String  empName;

	private String   shiftStart;

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

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	} 



}
