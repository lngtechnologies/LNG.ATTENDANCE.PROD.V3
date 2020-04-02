package com.lng.dto.empFailedAttendance;

import java.util.Date;
public class EmpFailedAttendanceDto {

	private Integer empFailedAttendanceId;

	private Integer refCustId;

	private Integer refBrId;

	private String empAttendanceFlag;

	private Date empAttendanceDatetime;

	private String employeePicture;

	public Integer getEmpFailedAttendanceId() {
		return empFailedAttendanceId;
	}

	public void setEmpFailedAttendanceId(Integer empFailedAttendanceId) {
		this.empFailedAttendanceId = empFailedAttendanceId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public Integer getRefBrId() {
		return refBrId;
	}

	public void setRefBrId(Integer refBrId) {
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

	public String getEmployeePicture() {
		return employeePicture;
	}

	public void setEmployeePicture(String employeePicture) {
		this.employeePicture = employeePicture;
	}
}
