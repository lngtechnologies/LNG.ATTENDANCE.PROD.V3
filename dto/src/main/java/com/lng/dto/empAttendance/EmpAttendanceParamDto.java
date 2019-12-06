package com.lng.dto.empAttendance;

import java.sql.Time;
import java.util.Date;

public class EmpAttendanceParamDto {


	private Integer empAttendanceId;
	
	private  Integer refEmpId;
	
	private  String  empName;

	private Integer deptId;
	
	private String shiftName;
	
	private String   shiftStart; 
	
	private String shiftEnd;

	private String empAttendanceInDatetime;
	
	private String empAttendanceOutDatetime;
	
	private String empAttendanceDate;

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

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}

	public String getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(String shiftEnd) {
		this.shiftEnd = shiftEnd;
	}

	public String getEmpAttendanceInDatetime() {
		return empAttendanceInDatetime;
	}

	public void setEmpAttendanceInDatetime(String empAttendanceInDatetime) {
		this.empAttendanceInDatetime = empAttendanceInDatetime;
	}

	public String getEmpAttendanceOutDatetime() {
		return empAttendanceOutDatetime;
	}

	public void setEmpAttendanceOutDatetime(String empAttendanceOutDatetime) {
		this.empAttendanceOutDatetime = empAttendanceOutDatetime;
	}

	public String getEmpAttendanceDate() {
		return empAttendanceDate;
	}

	public void setEmpAttendanceDate(String empAttendanceDate) {
		this.empAttendanceDate = empAttendanceDate;
	}

	

	
}
