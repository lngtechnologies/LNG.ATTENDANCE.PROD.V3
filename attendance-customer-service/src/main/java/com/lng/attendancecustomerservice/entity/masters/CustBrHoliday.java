package com.lng.attendancecustomerservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tmcustbrholiday")
public class CustBrHoliday {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custBrHolidayId")
	private  Integer custBrHolidayId;
	
	@ManyToOne
	@JoinColumn(name = "refbrId")
	private Branch  branch;
	
	@ManyToOne
	@JoinColumn(name = "refHolidayId")
	private  HolidayCalendar holidayCalendar;

	public Integer getCustBrHolidayId() {
		return custBrHolidayId;
	}

	public void setCustBrHolidayId(Integer custBrHolidayId) {
		this.custBrHolidayId = custBrHolidayId;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public HolidayCalendar getHolidayCalendar() {
		return holidayCalendar;
	}

	public void setHolidayCalendar(HolidayCalendar holidayCalendar) {
		this.holidayCalendar = holidayCalendar;
	}
	
	
	
	

}
