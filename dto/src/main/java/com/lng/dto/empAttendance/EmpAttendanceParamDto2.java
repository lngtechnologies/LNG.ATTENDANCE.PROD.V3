package com.lng.dto.empAttendance;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class EmpAttendanceParamDto2 {
	
	private  Time   Times;
	
	private String  emp;
	
	private   Integer  refCustId;
	
	private Date empAttendanceDatetime;
	
	private  Integer refEmpId;

	private  String  empName;
	
	private String   shiftStart;
	
	private  Date  empAttendanceConsiderDatetime;
	
	public Time getTimes() {
		return Times;
	}

	public void setTimes(Time times) {
		Times = times;
	}

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

	public Date getEmpAttendanceDatetime() {
		return empAttendanceDatetime;
	}

	public void setEmpAttendanceDatetime(Date empAttendanceDatetime) {
		this.empAttendanceDatetime = empAttendanceDatetime;
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

	public Date getEmpAttendanceConsiderDatetime() {
		return empAttendanceConsiderDatetime;
	}

	public void setEmpAttendanceConsiderDatetime(Date empAttendanceConsiderDatetime) {
		this.empAttendanceConsiderDatetime = empAttendanceConsiderDatetime;
	}

	

}
