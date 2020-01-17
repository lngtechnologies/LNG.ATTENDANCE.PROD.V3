package com.lng.dto.empAttendance;

public class EmpManualAttendanceParamDto {
	
	private String  emp;

	private   Integer  refCustId;

	private String empAttendanceDate;

	private  Integer refEmpId;

	private  String  empName;

	private String shiftStart;

	private String shiftEnd;

	private  String  empAttendanceInDatetime;

	private  String  empAttendanceOutDatetime;

	private String empAttendanceConsiderInDatetime;

	private String empAttendanceConsiderOutDatetime;
	
	private Integer loginId;

	public String getEmp() {
		return emp;
	}

	public void setEmp(String emp) {
		this.emp = emp;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public String getEmpAttendanceDate() {
		return empAttendanceDate;
	}

	public void setEmpAttendanceDate(String empAttendanceDate) {
		this.empAttendanceDate = empAttendanceDate;
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

	public String getEmpAttendanceConsiderInDatetime() {
		return empAttendanceConsiderInDatetime;
	}

	public void setEmpAttendanceConsiderInDatetime(String empAttendanceConsiderInDatetime) {
		this.empAttendanceConsiderInDatetime = empAttendanceConsiderInDatetime;
	}

	public String getEmpAttendanceConsiderOutDatetime() {
		return empAttendanceConsiderOutDatetime;
	}

	public void setEmpAttendanceConsiderOutDatetime(String empAttendanceConsiderOutDatetime) {
		this.empAttendanceConsiderOutDatetime = empAttendanceConsiderOutDatetime;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}
	
	
}
