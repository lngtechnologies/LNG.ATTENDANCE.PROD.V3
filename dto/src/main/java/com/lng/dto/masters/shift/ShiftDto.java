package com.lng.dto.masters.shift;

public class ShiftDto {
	private Integer shiftId;
	private Integer refBrId;
	private String  shiftName;
	private String  shiftStart;
	private String    shiftEnd;
	private String brName;
	private Boolean shiftIsActive;
	
	public Integer getShiftId() {
		return shiftId;
	}
	public void setShiftId(Integer shiftId) {
		this.shiftId = shiftId;
	}
	public Integer getRefBrId() {
		return refBrId;
	}
	public void setRefBrId(Integer refBrId) {
		this.refBrId = refBrId;
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

	public String getBrName() {
		return brName;
	}
	public void setBrName(String brName) {
		this.brName = brName;
	}
	public Boolean getShiftIsActive() {
		return shiftIsActive;
	}
	public void setShiftIsActive(Boolean shiftIsActive) {
		this.shiftIsActive = shiftIsActive;
	}
	
}
