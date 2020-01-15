package com.lng.dto.empAttendance;

import java.util.Date;

public class EmpAttendanceParamDto2 {

	private String  emp;

	private   Integer  refCustId;

	private Date empAttendanceDate;

	private  Integer refEmpId;

	private  String  empName;

	private String shiftStart;

	private String shiftEnd;

	private  Date  empAttendanceInDatetime;

	private  Date  empAttendanceOutDatetime;

	private Date empAttendanceConsiderInDatetime;

	private Date empAttendanceConsiderOutDatetime;

	private Integer  loginId;

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

	public Date getEmpAttendanceDate() {
		return empAttendanceDate;
	}

	public void setEmpAttendanceDate(Date empAttendanceDate) {
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

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

}
