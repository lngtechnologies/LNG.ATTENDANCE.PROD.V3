package com.lng.attendancecustomerservice.entity.masters;

import java.sql.Time;

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
	private Time shiftStart;
	
	@Column(name = "shiftEnd")
	private Time shiftEnd;

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

	public Time getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(Time shiftStart) {
		this.shiftStart = shiftStart;
	}

	public Time getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(Time shiftEnd) {
		this.shiftEnd = shiftEnd;
	}
	
	
}
