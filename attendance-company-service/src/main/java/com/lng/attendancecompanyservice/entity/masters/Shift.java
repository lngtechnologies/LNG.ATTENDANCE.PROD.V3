package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tmshift")
public class Shift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shiftId")
	private Integer shiftId;
	
	@ManyToOne
	@JoinColumn(name = "refBrId")
	private Branch branch;
	
	@Column(name = "shiftName")
	private String shiftName;
	
	@Column(name = "shiftStart")
	private String shiftStart;
	
	@Column(name = "shiftEnd")
	private String shiftEnd;
	

	@Column(name = "defaultOutInhrs")
	private Integer defaultOutInhrs;

	public Integer getShiftId() {
		return shiftId;
	}

	public void setShiftId(Integer shiftId) {
		this.shiftId = shiftId;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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

	public Integer getDefaultOutInhrs() {
		return defaultOutInhrs;
	}

	public void setDefaultOutInhrs(Integer defaultOutInhrs) {
		this.defaultOutInhrs = defaultOutInhrs;
	}

	
	
}
